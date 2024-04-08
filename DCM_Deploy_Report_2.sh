#!/bin/bash

# Function to generate HTML header
generate_header() {
	

    cat <<EOF
<!DOCTYPE html>
<html>
<head>
    <title>DCM Deployment Report</title>
    <style>
	 .toggle-button {
            cursor: pointer;
            text-decoration: underline;
            color: blue;
        }

        .expanded-list {
            display: none;
        }

        .expanded-list.show {
            display: block;
        }
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid #dddddd;
            text-align: left;
            padding: 8px;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>DCM Deployment Report</h1>
<table>
    <tr>
        <th>PR Number</th>
        <th>Commit SHA</th>
        <th>Commit Message</th>
        <th>Files Changed</th>
    </tr>
EOF
}

# Function to generate HTML footer
generate_footer() {
    cat <<EOF
</table>
<script>
    function toggleList() {
        var list = document.getElementById("expandable-list");
        if (list.classList.contains("show")) {
            list.classList.remove("show");
            document.querySelector('.toggle-button').textContent = "Files";
        } else {
            list.classList.add("show");
            document.querySelector('.toggle-button').textContent = "Hide File List";
        }
    }
</script>
</body>
</html>
EOF
}

# Function to generate HTML table row for PR
generate_table_row() {
    pr_number=$1
    pr_url=$2
    commit_sha=$3
    commit_message=$4
    files_changed=$5

    echo "<tr>"
    #echo "<td><a title=$pr_number href=$pr_url></a>"
	echo "<td><a href=\"$pr_url\">$pr_number</a></td>"
    echo "<td>$commit_sha</td>"
    echo "<td>$commit_message</td>"
    
	td_files="<td><span class='toggle-button' onclick='toggleList()'>Files</span><ul class='expanded-list' id='expandable-list'>"
	
    for file_name in $files_changed; do
        td_files="$td_files<li>$file_name</li>"
		
    done
   
   td_files="$td_files</ul></td>"

	#echo "<td><ul>"
    #for file in $files_changed; do
     #   echo "<li>$file</li>"
    #done
    #echo "</ul></td>"
	echo $td_files
    echo "</tr>"
}

# Function to generate HTML report
generate_report() {
    generate_header
    # Get merged PRs
    pr_list_output=$(gh pr list --state merged  --base develop-dcm-june-2024 --json number,title,url)

    # Loop through PRs and extract information
    pr_count=$(echo "$pr_list_output" | jq length)
    for ((i = 0; i < pr_count; i++)); do
        pr_number=$(echo "$pr_list_output" | jq -r ".[$i].number")
        pr_url=$(echo "$pr_list_output" | jq -r ".[$i].url")
        pr_title=$(echo "$pr_list_output" | jq -r ".[$i].title")

        # Get commit SHA and message for the PR
        commit_info=$(gh pr view "$pr_number" --json headRefOid,headRefName,title,body)
        commit_sha=$(echo "$commit_info" | jq -r '.headRefOid')
        commit_message=$(echo "$commit_info" | jq -r '.title')

        # Get files changed in the PR
        files_changed=$(gh pr diff "$pr_number" --name-only)
 # files_changed=$(gh pr diff "$pr_number" --stat | grep "|" | awk -F "|" '{print $1}' | sed 's/^[ \t]*//')

        generate_table_row "$pr_number" "$pr_url" "$commit_sha" "$commit_message" "$files_changed"
    done

    generate_footer
}

# Generate the report and save to a file
generate_report > deployed_pr_report.html

echo "Deployed PR report generated: deployed_pr_report.html"

