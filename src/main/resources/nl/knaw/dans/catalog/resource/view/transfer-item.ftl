<#-- @ftlvariable name="" type="nl.knaw.dans.catalog.resource.ArchiveDetailView" -->
<!DOCTYPE html>
<html>

<head>
    <title>Dataset ${transferItem.nbn}</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
</head>

<body>
    <div class="container-sm">
        <h1>${transferItem.nbn}</h1>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th scope="col">Key</th>
                    <th scope="col">Value</td>
                </tr>
            </thead>
            <tbody>
                <tr><th scope="row">BAG ID</th><td>${transferItem.bagId}</td></tr>
                <tr><th scope="row">Object version</th><td>${transferItem.objectVersion}</td></tr>
                <tr><th scope="row">Data station</th><td>${transferItem.datastation}</td></tr>
                <tr><th scope="row">Dataverse PID</th><td>${transferItem.dataversePid}</td></tr>
                <tr><th scope="row">Dataverse PID version</th><td>${transferItem.dataversePidVersion}</td></tr>
                <tr><th scope="row">NBN</th><td>${transferItem.nbn}</td></tr>
                <tr><th scope="row">Other ID</th><td>${transferItem.otherId}</td></tr>
                <tr><th scope="row">Other ID version</th><td>${transferItem.otherIdVersion}</td></tr>
                <tr><th scope="row">Sword client</th><td>${transferItem.swordClient}</td></tr>
                <tr><th scope="row">Sword token</th><td>${transferItem.swordToken}</td></tr>
                <tr><th scope="row">OCFL object path</th><td>${transferItem.ocflObjectPath}</td></tr>
                <tr><th scope="row">File pid to local path</th><td>${transferItem.filepidToLocalPath}</td></tr>
            </tbody>
        </table>

        <h2>Versions</h2>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th scope="col">Version</th>
                    <th scope="col">Archival date</td>
                    <th scope="col">TAR</td>
                </tr>
            </thead>
            <tbody>
                <#list otherTransferItems as other>
                    <tr>
                        <th scope="row">${other.versionMajor}.${other.versionMinor}</th>
                        <td>${other.tar.archivalDate}</td>
                        <td>${other.tar.tarUuid}</td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>

</body>

</html>
