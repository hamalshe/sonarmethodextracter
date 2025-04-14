# Sonar Issues Extractor

## Overview

This project provides a utility to extract Sonar issues from a Sonar portal and update an Excel file with method names.

## Prerequisites

*   `jq` command-line JSON processor (available on Linux and Cygwin)
*   Mobaxterm terminal (recommended for executing bash shell scripts)
*   Maven (for building the Java jar file)
*   Git (for checking out the project)

## Usage

### Checking Out the Project

1.  Open a terminal/command prompt and navigate to the directory where you want to check out the project.
2.  Run the command `git clone https://github.com/hamalshe/sonarmethodextracter.git` to check out the project.

### Building the Java Jar File

1.  Navigate to the project directory in your terminal/command prompt.
2.  Run the command `mvn clean install` to build the Java jar file. This will generate the `sonarmethodextracter.jar` file in the `target` directory.

### Downloading Sonar Issues

1.  Run the `sonar_issues_downloader.sh` script to download the list of Sonar issues and dump it in a CSV file. (Change the `url` value, as per your need)
2.  The script downloads issues in chunks of 400 records per page and will automatically stop when either:
    *   All available pages have been downloaded
    *   The total number of records downloaded reaches the 10,000 record limit imposed by the Sonar portal
3.  Open the downloaded CSV file (the file name is configurable in the `sonar_issues_downloader.sh` script with the `reportfilename` variable name) in a spreadsheet editor (e.g. Microsoft Excel).
4.  Save the file in the modern `.xlsx` file format. This is required for the `sme` script to work correctly.

### Extracting Method Names

1.  Copy the `sme` script to your `/bin` folder:
    *   If using Mobaxterm, execute `open /bin` to open the `/bin` folder in a new window, then copy the `sme` script into this folder.
    *   Alternatively, copy the script using the command line: `cp sme /bin`
2.  Note: The `sme` script is configured to use the Java jar file located at `D:\SonarMethodExtracterWorkspace\sonarmethodextracter\target\sonarmethodextracter.jar`. You may need to update this path in the script to match your own jar file location.
3.  Run the `sme` command with the following arguments:
    *   `-s` followed by the path to the source directory (absolute or relative)
    *   The `.xlsx` file saved in step 4 above (absolute or relative path)
    *   `-sheet` followed by the sheet name
4.  The script will invoke a Java jar file utility to read the Excel file, scan the code in the source directory, and update the `Method` column with method names.

## Preparation

Before running the `sme` command, ensure that:

*   You have inserted a column named `Method` in the Excel file (ideally at the end of the last column)

## Notes

*   The `sme` script assumes that the user has a valid Java installation and that the Java jar file utility is configured correctly.