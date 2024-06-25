#!/bin/bash

#!/bin/bash

echo "REPO Env. variable: $1"
repoName=$1
# Accessing environment variables
applicationName=$ModuleName
baseValue=$BaseBranch
headValue=$HeadBranch
repoName=$RepoName
ModuleKeywords=$Keywords
DirectoryPath=$DirectoryPath


# Using the environment variables
echo "applicationName: $applicationName"
echo "Base branch: $baseValue"
echo "Head branch: $headValue"
echo "Repository name: $repoName"


# Trim spaces from the beginning and end
repoName=$(echo "$repoName" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
echo "REPO NEW. variable: $repoName"


readInputFile(){
	# Read inout file and generate a map
	while IFS='= ' read -r k v; do
		v=$(echo "$v" | sed 's/\r//g')
		inputParams[$k]=$v
	done < ReleaseReportInput.txt	
}

generate_header(){
	#applicationName=${inputParams[applicationName]}    
	
    cat <<EOF
<!DOCTYPE html>
<html>
<head>
   <style>
	
/* Style the tab */
.tab {
  overflow: hidden;
  border: 1px solid #ccc;
  background-color: #f1f1f1;
}

/* Style the buttons inside the tab */
.tab button {
  background-color: inherit;
  float: left;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 14px 16px;
  transition: 0.3s;
  font-size: 17px;
}

/* Change background color of buttons on hover */
.tab button:hover {
  background-color: #ddd;
}

/* Create an active/current tablink class */
.tab button.active {
  background-color: #ccc;
}

/* Style the tab content */
.tabcontent {
  overflow: scroll;
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-top: none;
}
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
			/*white-space: nowrap;*/
            border: 1px solid #dddddd;
            text-align: left;
            padding: 8px;
			font-family:calibri;
        }
        th {
            background-color: #f2f2f2;
        }
		th input[type="text"] {
            padding: 4px;
			margin-top: 4px;
			display:block;
            box-sizing: border-box;
            border: 1px solid #ccc;
        }

        .enable-filter ::after {
            content: "  ▼";
			color: black;
        }
		
		.enable-filter.active ::after {
            color: red;
        }
    </style>
</head>
<body>

<h1  style="font-family:calibri;font-size:30px;" align="center">$applicationName Release Report </h1>
<div class="tab">
	<button class="tablinks active" onclick="openTab(event, 'PullRequestsDiv')" id='PullRequests'>Pull Requests</button>
	<button class="tablinks" onclick="openTab(event, 'ConsolidatedFilesDiv')" id='ConsolidatedFiles'>Consolidated Files</button>
	<button class="tablinks" onclick="openTab(event, 'AtRiskFilesDiv')" id='AtRiskFiles'>Impacted Files</button>
</div>
<div id="PullRequestsDiv" name="PullRequestsDiv" class="tabcontent" style="display:block">

EOF
}

# Function to generate HTML header for table
generate_table_header() {
    cat <<EOF	
<span  style="font-family:calibri;font-size:15px;" align="center">$1 Pull Requests</span>
<table id="$1-pr-table" class="display" style="width:100%">
	<thead>
		<tr>
			<th class="enable-filter"><span>PR Number</span></th>
			<th class="enable-filter"><span>PR Author</span></th>
			<th class="enable-filter" Style="width: 100px"><span>PR Title</span></th>
			<th class="enable-filter"><span>Sprint</span></th>
			<th><span>Files Changed</span></th>
			<th><span>Additions</span></th>
			<th><span>Deletions</span></th>
			<th><span>FilesChanged</span></th>
			<th class="enable-filter"><span>SonarQube Check</span></th>
			<th class="enable-filter"><span>Created At</span></th>
			<th class="enable-filter"><span>Status Check</span></th>
			
		</tr>
	</thead>
	<tbody>
EOF
}

# Function to generate HTML footer
generate_table_footer() {
    cat <<EOF
</tbody></table>
EOF
}

generate_footer() {
    cat <<'EOF'
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.11.5/datatables.min.js"></script>
<script>
    function openTab(evt, tabName) {
      var i, tabcontent, tablinks;
      tabcontent = document.getElementsByClassName("tabcontent");
      for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
      }
      tablinks = document.getElementsByClassName("tablinks");
      for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
      }
      document.getElementById(tabName).style.display = "block";
      evt.currentTarget.className += " active";
    }

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
        var button = document.getElementById("prDesc_"+pr_number);

        if (content.style.display === "block") {
            content.style.display = "none";
            button.classList.remove("active");
        } else {
            content.style.display = "block";
            button.classList.add("active");
        }
    }
    
    //initialize datable for merged pr
    var merged_pr_table = $('#merged-pr-table').DataTable({
        searching: true,
        paging: false,
        pageLength: 20,
        ordering: false
    });
    
    //create filter for merged pr table column with .enable-filter class
    $('#merged-pr-table th.enable-filter').each(function () {
        var column = merged_pr_table.column($(this).index());
        var header = $(column.header());
        var input = $('<input style="display:none;" type="text" placeholder="Search..."/>')
            .appendTo(header)
            .on('keyup change clear', function () {
                if (column.search() !== this.value) {
                    column
                        .search(this.value)
                        .draw();
                }
                // Check if the input is empty or not
                if (this.value) {
                    $(header).addClass('active');
                } else {
                    $(header).removeClass('active');
                }
            });
    });

    //initialize datable for open pr
    var opent_pr_table = $('#open-pr-table').DataTable({
        searching: true,
        paging: false,
        pageLength: 20,
        ordering: false
    });
    
    //create filter for open pr table column with .enable-filter class
    $('#open-pr-table th.enable-filter').each(function () {
        var column = opent_pr_table.column($(this).index());
        var header = $(column.header());
        var input = $('<input style="display:none;" type="text" placeholder="Search..."/>')
            .appendTo(header)
            .on('keyup change clear', function () {
                if (column.search() !== this.value) {
                    column
                        .search(this.value)
                        .draw();
                }
                // Check if the input is empty or not
                if (this.value) {
                    $(header).addClass('active');
                } else {
                    $(header).removeClass('active');
                }
            });
    });
    
    //toggling search box
    $(".enable-filter span").click(function () {
        $(this).siblings('input').toggle();
        // Check if the input is empty or not
        if ($(this).siblings('input').val()) {
            $(this).parent().addClass('active');
        } else {
            $(this).parent().removeClass('active');
        }
    });

    // hiding not required components of datatable
    $('.dataTables_filter').hide();
    $('.dataTables_length').hide();
    $('.dataTables_info').hide();
</script>

</body>
</html>
EOF
}

# Function to generate HTML table row for PR
generate_table_row() {
 	
	#Conveting the Merged time to EST
	est_time=$(date -d "$pr_createdAt" +"%Y-%m-%d %H:%M:%S %Z")
   
    echo "<tr>"
    #echo "<td><a title=$pr_number href=$pr_url></a>"
	echo "<td><a href=\"$pr_url\" target='_blank'>$pr_number</a></td>"
    echo "<td>$pr_author</td>"
    echo "<td>$pr_title</td>"
    echo "<td>$sprint_name</td>"
#	echo "<td><button class='collapsible-button' onclick='toggleCollapsible($pr_number)' id='prDesc_$pr_number'>PR Description</button><div class='collapsible-content' id='collapsibleContent$pr_number'><p>$pr_description</p></div></td>"

	td_files="<td><button class='toggle-button' onclick='toggleList($pr_number)' id=$pr_number>Files</button><ul class='expanded-list' id='expandable-list$pr_number'>"
    for file_name in $files_changed; do
        td_files="$td_files<li>$file_name</li>"
    done
	td_files="$td_files</ul></td>"

	echo $td_files
	echo "<td>$additions</td>"
	echo "<td>$deletions</td>"
	echo "<td>$displaychangedFiles $fileChangedDetail</td>"
    echo "<td><a href=\"https://sonarqubeenterprise.pgcloud.com/sonarqube/dashboard?id=plm-3dx&pullRequest=$pr_number\" target='_blank'>$pr_number</a></td>"
	echo "<td>$est_time</td>"
	echo "<td>$pr_Check_state</td>"
	echo "</tr>"
}


declare -A grouped_files
# Function to generate consolidated list of files for all PR
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
	
	# Iterate over the file paths
	for file_path in "${consolidated_files_array[@]}"; do
		# Extract the first folder name
		first_folder=$(echo "$file_path" | cut -d'/' -f1)
		if [ -n "$first_folder" ]; then
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
		fi
	done	
	
	JAVAC_COMMAND="$JAVA_HOME/bin/javac"
	JAVA_COMMAND="$JAVA_HOME/bin/java"
	CURR_DIR=$(pwd)
	JAVA_SOURCE="AnalyzeSchemaChanges.java"
	JAVA_CLASS="AnalyzeSchemaChanges"
	echo $JAVAC_COMMAND
	echo $JAVAC_COMMAND
	# Function to compile the Java program if necessary
	compile_java() {
		if [ ! -f "$CURR_DIR/$JAVA_CLASS.class" ] || [ "$CURR_DIR/$JAVA_SOURCE" -nt "$CURR_DIR/$JAVA_CLASS.class" ]; then
			$JAVAC_COMMAND "$CURR_DIR/$JAVA_SOURCE"
			if [ $? -ne 0 ]; then
				echo "Failed to compile Java program."
				exit 1
			fi
		fi
	}
	compile_java
	# Run the Java program and capture its output
	JAVA_OUTPUT=$($JAVA_COMMAND -cp "$CURR_DIR" "$JAVA_CLASS")
	#echo $JAVA_OUTPUT
	
	atRisk_files=()
	# Iterate over the file paths to find at risk files
	for file_path in "${consolidated_files_array[@]}"; do
		modulespecificflag=false
		for search_string in "${keywordsArray[@]}"; do
			# Check if the filename contains the current search string
			if [[ "$file_path" == *"$search_string"* ]]; then
				modulespecificflag=true
				break
			fi
		done
			 
		if [ "$modulespecificflag" == false ]; then
			atRisk_files+=("$file_path")
		fi
	done	
	
	# Check JAVA_OUTPUT and append if it matches specific files
	IFS=$'\n' read -rd '' -a java_output_lines <<< "$JAVA_OUTPUT"
	for line in "${java_output_lines[@]}"; do
			atRisk_files+=("$line")
	done


	#echo "<html><body>"
	echo "</div><div id=\"ConsolidatedFilesDiv\" name=\"ConsolidatedFilesDiv\" class=\"tabcontent\" style=\"display:none\">"
	echo "<h2>Consolidated File List</h2>"
	echo "<table border=\"1\">"
   
	for folder_name in "${!grouped_files[@]}"; do
		echo "<tr>"
		echo "<td><button class='toggle-button' onclick='togglefolder(\"$folder_name\")' id='$folder_name'>$folder_name</button>"
		#</td>"
		#echo "<td>"
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
    echo "</div>"
    #echo "</body></html>"
#Print At Risk Files
	echo "<div id=\"AtRiskFilesDiv\" name=\"AtRiskFilesDiv\" class=\"tabcontent\" style=\"display:none\">"
	echo "<h2>Impacted Files List</h2>"
	echo "<table border=\"1\">"
   
	for eachfilename in "${atRisk_files[@]}"; do
		echo "<tr>"
		echo "<td>$eachfilename</td>"
		echo "</tr>"
	done
    
	echo "</table>"
    echo "</div>"
	
    #echo "</body></html>"
}

get_file_status() {
    commit_hash=$1
    file_path=$2

    # Get the status of the file in the given commit
    status=$(git show --name-status "${commit_hash}" -- "${file_path}" | head -n1 | awk '{print $1}')
#	git show --name-status "a300653d78d0e5b5d59a5d14f214a51edc04ab71" -- "SpinnerPagePrograms/Business/PageFiles/pgDesignIntelegenceGUI"

    echo "${status}"
}
# Function to get sprint name and date info 
getPRAddDelFileCount() {
	
	declare -A global_map
	#local repoName=${inputParams[repoName]}    

	commits_count=$(echo "$pr_commits" | jq length)
	for ((j = 0; j < commits_count; j++)); do
		sha=$(echo "$pr_commits" | jq -r ".[$j].oid")
		date=$(echo "$pr_commits" | jq -r ".[$j].committedDate")
		#fileInfo=$(gh api -H "Accept: application/vnd.github.v3+json" repos/$repo_owner/$repo_name/commits/$sha --jq '.files[] | {status: '.status', filename: '.filename'}')
		fileInfo=$(gh api -H "Accept: application/vnd.github.v3+json" repos/$repoName/commits/$sha | jq -r '.files[] | "\(.filename):\(.status)"')
		# Update the global map
		while IFS= read -r file_status; do
			filename="${file_status%%:*}"
			status="${file_status#*:}"
			# Update the status in the global map
			if [[ -v "global_map[$filename]" ]]; then
			
				prev_status="${global_map[$filename]}"
				# Checking the conditions and updating the global_map array
				if [[ "$prev_status" == "added" ]] && [[ "$status" == "removed" ]]; then
					unset global_map[$filename]
				elif [[ "$prev_status" == "removed" ]] && [[ "$status" == "added" ]]; then
					unset global_map[$filename]
				elif [[ "$status" == "modified" ]] && ([[ "$prev_status" == "added" ]] || [[ "$prev_status" == "removed" ]]); then
					global_map[$filename]="$prev_status"
				fi
			else
				global_map[$filename]="$status"
			fi
		done <<< "$fileInfo"
	done
			
	added_count=0
	removed_count=0
	modified_count=0

	for filename in "${!global_map[@]}"; do
		status="${global_map[$filename]}"
		if [[ "$status" == "added" ]]; then
			added_count=$((added_count + 1))
		elif [[ "$status" == "removed" ]]; then
			removed_count=$((removed_count + 1))
		else
			modified_count=$((modified_count + 1))
		fi
	done

	echo "{\"addFileCount\": \"$added_count\", \"delFileCount\": \"$removed_count\",\"modFileCount\": \"$modified_count\"}"

}
# Function to get sprint name and date info 
getSprintDateData() {
	
	#local repoName=${inputParams[repoName]}    
	#local filter="${inputParams[head]}-*"   
	local filter="${baseValue}-*"   
	# Fetch tags and their commit SHAs
	local tags=$(gh api repos/$repoName/tags --jq '.[] | {name: '.name', sha: '.commit.sha'}' |  grep "$filter")
	# Declare the array
	local sprintDataArray="["
	# Process each tag
	while read -r tag; do  
		local tagName=$(echo "$tag" | jq -r '.name')
		local commitSha=$(echo "$tag" | jq -r '.sha')
		# fetch commit details
		local tempDate=$(gh api repos/$repoName/git/commits/$commitSha --jq '{date: .committer.date}' 2>/dev/null | jq -r '.date')
		commitDate=$(date -d "$tempDate" +"%Y-%m-%d")
		# create json array
		sprintDataArray+="{\"sprintName\": \"$tagName\", \"sprintEndDate\": \"$commitDate\"},"
		#echo "tagName: $tagName commitDate: $commitDate"
	done <<< "$(echo "$tags")"
	# Remove trailing comma and close array
	sprintDataArray="${sprintDataArray%,}]" 
	# Sort the json array by sprintEndDate using jq
	sprintDataArray=$(echo "$sprintDataArray" | jq 'sort_by(.sprintEndDate)')
	echo "$sprintDataArray"
}

# Function to generate HTML report
generate_PR_data_table() {
	stateFilter=$1
	stateValue=${inputParams[state]}
	#headValue=${inputParams[head]}  
	#baseValue=${inputParams[base]}         
	#repoName=${inputParams[repoName]}    
	repo_path=${inputParams[repoPath]}
	
	#$applicationName $releaseName $serverName $repoName $baseValue $stateFilter    
	
	# Initialize associative array to store unique changed files
	declare -A unique_changed_files

	stateFilter=$(echo "$stateFilter" | tr '[:upper:]' '[:lower:]')
	
	# Get merged PRs
	pr_list_output=$(gh pr list --repo $repoName --state=$stateFilter  --base=$baseValue --head=$headValue --json number,title,url,author,createdAt,additions,deletions,changedFiles,mergeStateStatus,body,statusCheckRollup,commits)

    # Loop through PRs and extract information
    pr_count=$(echo "$pr_list_output" | jq length)

	if [ "${pr_count}" -gt 0 ]; then
		generate_table_header $stateFilter
		
		# get sprint name and sprint tagging date data
		sprintDateDataArray=$(getSprintDateData)
		
		for ((i = 0; i < pr_count; i++)); do
			
				pr_createdAt=$(echo "$pr_list_output" | jq -r ".[$i].createdAt")		
				
				sprintForPR=$(echo "$sprintDateDataArray" | jq --arg date "$pr_createdAt" '
				[.[] | select(.sprintEndDate > $date)] | .[0]')

				sprint_name=$(echo "$sprintForPR" | jq -r '.sprintName')
				pr_number=$(echo "$pr_list_output" | jq -r ".[$i].number")
				pr_url=$(echo "$pr_list_output" | jq -r ".[$i].url")
				pr_title=$(echo "$pr_list_output" | jq -r ".[$i].title")
				pr_author=$(echo "$pr_list_output" | jq -r ".[$i].author")
				#get only login name of author
				pr_author=$(echo "$pr_author" | jq -r '.login')
				
				pr_body=$(echo "$pr_list_output" | jq -r ".[$i].body")
				pr_Check=$(echo "$pr_list_output" | jq -r ".[$i].statusCheckRollup")
				pr_mergeStateStatus=$(echo "$pr_list_output" | jq -r ".[$i].mergeStateStatus")

				additions=$(echo "$pr_list_output" | jq -r ".[$i].additions")		
				deletions=$(echo "$pr_list_output" | jq -r ".[$i].deletions")		
				changedFiles=$(echo "$pr_list_output" | jq -r ".[$i].changedFiles")	
				displaychangedFiles="<br>Total:$changedFiles"				
		
				pr_commits=$(echo "$pr_list_output" | jq -r ".[$i].commits")						
				# get add/delete file count for PR
				prAddDekFileCount=$(getPRAddDelFileCount "$pr_commits")
				addFileCount=$(echo "$prAddDekFileCount" | jq -r ".addFileCount")		
				delFileCount=$(echo "$prAddDekFileCount" | jq -r ".delFileCount")		
				modFileCount=$(echo "$prAddDekFileCount" | jq -r ".modFileCount")				
				fileChangedDetail="<br>(Added:$addFileCount<br>Deleted:$delFileCount<br>Modified:$modFileCount)"
				if [[ -z "$pr_Check" || "$pr_Check" == "null" ]]; then
					echo "No status check rollup data available."
					pr_Check_state="NA"
				else 
					pr_Check_state=$(echo "$pr_Check" | jq -r '.[1].state')
				fi
			
				if [[ "$pr_Check_state" == "null" ]]; then
					pr_Check_state="NA"
				fi
				
				if [[ "$sprint_name" == "null" ]]; then
					sprint_name="NA"
				fi
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
				
				# Loop through changed files
				for file in $files_changed; do
					# Add file to associative array
					unique_changed_files["$file"]=1
				done
				
				echo $file_lists
								
				if [[ -z "$stateFilter" || "$stateFilter" == "open" ]]; then
					pr_Check_state=$pr_mergeStateStatus
					generate_table_row "$pr_number" "$pr_url" "$pr_createdAt" "$pr_title" "$files_changed" "$pr_author" "$sprint_name" "$pr_Check_state"
				else 
					generate_table_row "$pr_number" "$pr_url" "$pr_createdAt" "$pr_title" "$files_changed" "$pr_author" "$sprint_name" "$pr_Check_state"
				fi
				#generate_table_row "$pr_number" "$pr_url" "$pr_createdAt" "$pr_title" "$files_changed" "$pr_author" "$sprint_name" "$pr_mergeStateStatus"
		done

		generate_table_footer
	else
		echo "No $stateFilter Pull Requests Found"
	fi

	#if [[ -z "$stateFilter" || "$stateFilter" == "merged" ]]; then
		# Get the list of unique changed files (keys of the associative array)
#		consolidated_files=$(printf "%s\n" "${!unique_changed_files[@]}" | sort -u)
#		mapfile -t consolidated_files_array <<< "$consolidated_files"
#		generate_consolidated_file "${consolidated_files_array[@]}"
#	fi

if [[ -z "$stateFilter" || "$stateFilter" == "merged" ]]; then
		# Get the list of unique changed files (keys of the associative array)
		consolidated_files=$(printf "%s\n" "${!unique_changed_files[@]}" | sort -u)
		mapfile -t consolidated_files_array <<< "$consolidated_files"
#		generate_consolidated_file "${consolidated_files_array[@]}"
	fi
}



declare -A inputParams=( )
readInputFile $inputParams

# Generate the report and save to a file

#ModuleKeywords=${inputParams[DCMKeywords]}
echo "ModuleKeywords---- $ModuleKeywords"
# Convert comma-separated string to array
IFS=',' read -r -a keywordsArray <<< "$ModuleKeywords"
echo "keywordsArray---- $keywordsArray"
#applicationName=${inputParams[applicationName]}
report_file="${applicationName}_Release_Report.html"


to_recipient=${inputParams[SendMailTo]}
cc_recipients=${inputParams[SendMailCC]}
from_email=${inputParams[from_email]}
touch "$report_file"

generate_header > "$report_file"
#generate_tabs_html >> "$report_file"
#$releaseName $serverName $repoName $baseValue 

generate_PR_data_table "Merged" >> "$report_file"
generate_PR_data_table "Open" >> "$report_file"
generate_consolidated_file "${consolidated_files_array[@]}" >> "$report_file"
generate_footer >> "$report_file"

date_time=$(date +"%m-%d-%Y %I:%M:%S %p")
mail_body="Please find attached the Release report for $applicationName from plm-3dx Repo."

echo "$mail_body" | mailx -s "Release Report for $applicationName" -c "$cc_recipients" -a "$report_file"  -S smtp="smtp://127.0.0.1" -S from="$from_email" "$to_recipient"
echo "Release report generated"

read -p "Press any key to continue..."
