#!/bin/bash

readInputFile(){
	# Read inout file and generate a map
	while IFS='= ' read -r k v; do
		v=$(echo "$v" | sed 's/\r//g')
		inputParams[$k]=$v
	done < AgileReportInput.txt	
}

# Function to generate HTML header
generate_header() {
    cat <<EOF
<!DOCTYPE html>
<html>
<head>
    <style>
		 /* Style for the collapsible content */
		.collapsible-content {
			display: none; /* Initially hidden */
			overflow: hidden; /* Hide overflowing content */
		}

		/* Style for the collapsible button */
		.collapsible-button {
			background-color: #f0f0f0;
			padding: 10px;
			border: none;
			text-align: left;
			width: 100%;
			cursor: cell;
		}
	
		/* Style for the button when active (expanded) */
		.active {
			background-color: #ddd;
		}
		.toggle-button {
			background-color: #f0f0f0;
			padding: 10px;
			border: none;
			text-align: left;
			width: 100%;
			cursor: cell;
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
			font-family:calibri;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1  style="font-family:calibri;font-size:30px;" align="center">$1 Deployment Report for $2</h1>
<p style="font-family:calibri;font-size:15px;">Server Name:	<b>$3 </b> <br> 
Repo Name:	<b>$4</b><br> 
Branch Name:<b>$5</b></p>
<table>
    <tr>
        <th>PR Number</th>
        <th>PR Author</th>
		<th>PR Title</th>
        <th>PR Description</th>
        <th>Files Changed</th>
		<th>SonarQube Check</th>
		<th>Merged At</th>
        
    </tr>
EOF
}

# Function to generate HTML footer
generate_footer() {
    cat <<EOF
</table>
<script>
    function toggleList(pr_number) {
        var list = document.getElementById("expandable-list"+pr_number);
        if (list.classList.contains("show")) {
            list.classList.remove("show");
           document.querySelector('.toggle-button').textContent = "Files";
        } else {
            list.classList.add("show");
            document.querySelector('.toggle-button').textContent = "Files";
        }
    }
	
	function toggleCollapsible(pr_number) {
		var content = document.getElementById("collapsibleContent"+pr_number);
		var button = document.getElementById("prDesc_"+$pr_number);

		if (content.style.display === "block") {
			content.style.display = "none";
			button.classList.remove("active");
		} else {
			content.style.display = "block";
			button.classList.add("active");
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
    pr_mergedAt=$3
    pr_title=$4
    files_changed=$5
	pr_author=$6
	pr_description=$7
   
   #Conveting the Merged time to EST
	est_time=$(date -d "$pr_mergedAt" +"%Y-%m-%d %H:%M:%S %Z")
    echo "<tr>"
    #echo "<td><a title=$pr_number href=$pr_url></a>"
	echo "<td><a href=\"$pr_url\" target='_blank'>$pr_number</a></td>"
    echo "<td>$pr_author</td>"
    echo "<td>$pr_title</td>"
   # echo "<td>$pr_description</td>"
	echo "<td><button class='collapsible-button' onclick='toggleCollapsible($pr_number)' id='prDesc_$pr_number'>PR Description</button><div class='collapsible-content' id='collapsibleContent$pr_number'><p>$pr_description</p></div></td>"

	IFS=$'\n' # Set the Internal Field Separator to newline	
	td_files="<td><button class='toggle-button' onclick='toggleList($pr_number)' id=$pr_number>Files</button><ul class='expanded-list' id='expandable-list$pr_number'>"
    for file_name in $files_changed; do
        td_files="$td_files<li>$file_name</li>"
    done
	td_files="$td_files</ul></td>"

	echo $td_files
    echo "<td><a href=\"https://sonarqubeenterprise.pgcloud.com/sonarqube/dashboard?id=plm-3dx&pullRequest=$pr_number\" target='_blank'>$pr_number</a></td>"
	echo "<td>$est_time</td>"
	echo "</tr>"
}
declare -A grouped_files
# Function to generate HTML table row for PR
generate_consolidated_file() {
cat <<EOF
<script>
function togglefolder(foldername) {
    var list = document.getElementById("expandable-list" + foldername);
    var button = document.getElementById(foldername); 
    if (list.classList.contains("show")) {
        list.classList.remove("show");
        button.textContent = foldername; 
    } else {
        list.classList.add("show");
        button.textContent = "Hide " + foldername + " Files"; 
    }
}
</script>
EOF
    # Receive array as an argument
    consolidated_files_array=("$@")
	IFS=$'\n' # Set the Internal Field Separator to newline	
	# Iterate over the file paths
	for file_path in "${consolidated_files_array[@]}"; do
		# Extract the first folder name
		first_folder=$(echo "$file_path" | cut -d'/' -f1)
		# Add the file path to the corresponding group in the associative array
		if [[ "$first_folder" == "WebWidget" || "$first_folder" == "WebWidgetSourceFiles" ]]; then
			# Append the file path to the merged array
			grouped_files[WebWidget]+="$file_path"$'\n'
		else 
			if [[ -z ${grouped_files[$first_folder]} ]]; then
				grouped_files[$first_folder]="$file_path"
			else
				grouped_files[$first_folder]+=$'\n'"$file_path"
			fi
		fi
	done	
	echo "<html>"
	echo "<body>"
	echo "<h2>Consolidated File List</h2>"
	echo "<table border="1">"
    echo "<tr>"
    echo "<th>Files</th>"
    echo "</tr>"
	for folder_name in "${!grouped_files[@]}"; do
		echo "<tr>"
		echo "<td><button class='toggle-button' onclick='togglefolder(\"$folder_name\")' id='$folder_name'>$folder_name</button></td>"
        echo "<td>"
        echo "<ul class='expanded-list' id='expandable-list$folder_name'>"
		IFS=$'\n' # Set the Internal Field Separator to newline	
        for file_name in ${grouped_files[$folder_name]}; do
            echo "<li>$file_name</li>"
        done
        echo "</ul>"
        echo "</td>"
		echo "</tr>"
	done
	echo "</table>"
    echo "</body>"
    echo "</html>"
}

# Function to generate HTML report
generate_report() {
	stateValue=${inputParams[state]}	
	baseValue=${inputParams[base]}    
	applicationName=${inputParams[applicationName]}    
	releaseName=${inputParams[releaseName]}    
	serverName=${inputParams[serverName]}    
	repoName=${inputParams[repoName]}    
	date_to_compare=${inputParams[date_to_compare]}
	repo_path=${inputParams[repoPath]}
	
	#date_to_compare="2024-04-17"
	generate_header $applicationName $releaseName $serverName $repoName $baseValue
    
	# Initialize associative array to store unique changed files
	declare -A unique_changed_files
	# Get merged PRs
	pr_list_output=$(gh pr list --repo $repoName --state=$stateValue  --base=$baseValue --json number,title,url,author,mergedAt,body)

    # Loop through PRs and extract information
    pr_count=$(echo "$pr_list_output" | jq length)
	  for ((i = 0; i < pr_count; i++)); do
        
		pr_mergedAt=$(echo "$pr_list_output" | jq -r ".[$i].mergedAt")
		
		if [[ "$pr_mergedAt" > "$date_to_compare" ]]; then
	
			pr_number=$(echo "$pr_list_output" | jq -r ".[$i].number")
			pr_url=$(echo "$pr_list_output" | jq -r ".[$i].url")
			pr_title=$(echo "$pr_list_output" | jq -r ".[$i].title")
			pr_author=$(echo "$pr_list_output" | jq -r ".[$i].author")
			#get only login name of author
			pr_author=$(echo "$pr_author" | jq -r '.login')
		 	pr_body=$(echo "$pr_list_output" | jq -r ".[$i].body")
			
			old_keyword="Requirement"
			new_keyword="User Story"
			
			#replace keyword "Requirement" by keyword "User Story"
			pr_title=$(echo "${pr_title//$old_keyword/$new_keyword}")
			
			# Replace new lines with <br> using sed	
			message=$(echo "$pr_body" | sed ':a;N;$!ba;s/\n/<br>/g')
			# Extract substring From PR message Usng start and end keywords
			startMsg="Related ALM Requirement#"
			endMsg="Related ALM Defect#"
			pr_description=$(echo "$message" | sed -n 's/.*\(.*\)$startMsg.*/\1/p')
		
			# Extract substring using awk
			substring=$(echo "$message" | awk -v start="Related ALM Defect#" -v end="Related ALM Requirement#" 'match($0, start ".*" end) {print substr($0, RSTART+length(start), RLENGTH-length(start)-length(end))}')


			# Get files changed in the PR
			files_changed=$(gh pr diff --repo $repoName "$pr_number" --name-only)
			
			IFS=$'\n' # Set the Internal Field Separator to newline	
			# Loop through changed files
			for file in $files_changed; do
				# Add file to associative array
				unique_changed_files["$file"]=1
			done
			generate_table_row "$pr_number" "$pr_url" "$pr_mergedAt" "$pr_title" "$files_changed" "$pr_author" "$substring"
		fi
	done

    generate_footer
	# Get the list of unique changed files (keys of the associative array)
	consolidated_files=$(printf "%s\n" "${!unique_changed_files[@]}" | sort -u)
	mapfile -t consolidated_files_array <<< "$consolidated_files"
	generate_consolidated_file "${consolidated_files_array[@]}"
}


declare -A inputParams=( )
readInputFile $inputParams

# Generate the report and save to a file
branch=${inputParams[base]}
report_file="${branch}_Deployment_Report.html"


to_recipient=${inputParams[SendMailTo]}
cc_recipients=${inputParams[SendMailCC]}
from_email=${inputParams[from_email]}
touch "$report_file"
generate_report >> "$report_file"

date_time=$(date +"%m-%d-%Y %I:%M:%S %p %Z")
mail_body="Please find attached the report for DCM build deployed on Platform Sandbox from plm-3dx Repo on at $date_time."

echo "$mail_body" | mailx -s 'Platform Sandbox Deployment Report for Sprint12 ' -c "$cc_recipients" -a "$report_file"  -S smtp="smtp://127.0.0.1" -S from="$from_email" "$to_recipient"
rm "$report_file"
echo "Deployed PR report generated"

