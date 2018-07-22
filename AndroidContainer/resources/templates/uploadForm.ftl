<html>
    <body>
        <h2>Upload Android Project</h2>
        <p>Upload a zip file for your project...</p>
        <form action="/api/uploadZip"
              enctype="multipart/form-data"
              method="post">

            <input type="text" name="projectName" value="Ex: My Project">
            <input type="file" name="zipFile">
            <button type="submit">Upload</button>

        </form>
        <p>... or submit a GitHub link to load</p>
        <form action="/api/gitDownload"
              enctype="multipart/form-data"
              method="post">

            <input type="text" name="projectName" value="Ex: My Project">
            <input type="text" name="gitPath" value="Ex: https://github.com/user/project.git">
            <button type="submit">Upload</button>

        </form>

    </body>
</html>