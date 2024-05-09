const fs = require('fs');
const path = require('path');

const directory = '../reports'; // Directory containing the reports

// Read files from the directory
fs.readdir(directory, (err, files) => {
    if (err) {
        console.error('Error reading directory:', err);
        return;
    }

    // Filter out non-HTML files
    const htmlFiles = files.filter(file => path.extname(file) === '.html');

    if (htmlFiles.length === 0) {
        console.log('No HTML files found in the directory.');
        return;
    }

    // Sort HTML files by creation time
    htmlFiles.sort((a, b) => {
        const statsA = fs.statSync(path.join(directory, a));
        const statsB = fs.statSync(path.join(directory, b));
        return statsB.birthtime.getTime() - statsA.birthtime.getTime();
    });

    // Generate table rows for each HTML file
    const tableRows = htmlFiles.map(file => {
        const stats = fs.statSync(path.join(directory, file));
        const creationDate = stats.birthtime.toLocaleString(); // Format creation date
        return `<tr><td>${creationDate}</td><td><a href="../testVSR5/reports/${file}">${file}</a></td></tr>`;
    });

    // Generate index.html content
    const htmlContent = `
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Report Index</title>
            <style>
                table {
                    border-collapse: collapse;
                    width: 100%;
                }
                th, td {
                    border: 1px solid #ddd;
                    padding: 8px;
                    text-align: left;
                }
                th {
                    background-color: #f2f2f2;
                }
            </style>
        </head>
        <body>
            <h1>Report Index</h1>
            <table>
                <thead>
                    <tr>
                        <th>Creation Date</th>
                        <th>File</th>
                    </tr>
                </thead>
                <tbody>
                    ${tableRows.join('\n')}
                </tbody>
            </table>
        </body>
        </html>
    `;

    // Write the content to index.html
    fs.writeFile('index.html', htmlContent, err => {
        if (err) {
            console.error('Error writing index.html:', err);
            return;
        }
        console.log('index.html generated successfully!');
    });
});
