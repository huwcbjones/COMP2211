#!/bin/bash
increment=0;

function errormsg(){
    echo "***********************************************************"
    echo "*                                                         *"
    echo "*                        ! ERROR !                        *"
    echo "*                                                         *"
    echo "***********************************************************"
    echo -e "\n"$1
}

# Get increment number
while getopts 'i:' flag; do
  case "${flag}" in
    i) increment="${OPTARG}" ;;
    *) echo "Unexpected option ${flag}" ; exit ;
  esac
done

if [ ${increment} -eq 0 ]
then
    errormsg "Please specify increment number using -i n"
    exit 1
fi

source_dir="team_16_increment${increment}"

echo -e \\n\# Fetching branches from repo
git fetch
echo -e \\n\# Checking out branch increment${increment}
if ! git checkout increment${increment} ;
then
    errormsg "Failed to checkout branch 'increment"${increment}"'.\nPlease create branch and try again."
    exit 1
fi

echo -e \\n\# Creating directories
mkdir -p ${source_dir}/code
mkdir -p ${source_dir}/documentation

echo -e \n\# Building project
mvn package

echo -e \\n\# Copying files
cp README.md ${source_dir}/
cp -r src ${source_dir}/code/
cp pom.xml ${source_dir}/code/
cp target/AdDashboard-0.1.0.jar ${source_dir}/increment${increment}.jar
cp docs/*.pdf ${source_dir}/documentation

echo -e \\n\# Zipping project
zip -r increment${increment}.zip ${source_dir}

echo -e \\n\# Cleaning up
rm -rf ${source_dir}