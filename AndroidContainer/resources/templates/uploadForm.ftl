<html>
    <body>
        <h2>Upload Android Project</h2>
        <p>Upload a zip file for your project...</p>
        <form action="/api/uploadZip"
              enctype="multipart/form-data"
              method="post">

            <input type="text" name="projectName" value="Ex: My Project">
            <input type="text" name="projectPath" value="Ex: MyProject/">
            <input type="text" name="packageName" value="Ex: io.myorg.myapp">
            <input type="text" name="appModule" value="Ex: app">
            <input type="file" name="zipFile">
            <button type="submit">Upload</button>

        </form>
        <p>... or submit a GitHub link to load</p>
        <form action="/api/gitDownload"
              enctype="multipart/form-data"
              method="post">

            <input type="text" name="projectName" value="BilityExampleApp">
            <input type="text" name="projectPath" value="">
            <input type="text" name="packageName" value="org.vontech.bilitytestapplication">
            <input type="text" name="appModule" value="app">
            <input type="text" name="gitPath" value="git@github.com:vontell/BilityTestApplication.git">
            <button type="submit">Upload</button>

        </form>

    </body>
</html>