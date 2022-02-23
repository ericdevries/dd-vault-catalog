<#-- @ftlvariable name="" type="nl.knaw.dans.catalog.resource.ArchiveDetailView" -->
<h1>${transferItem.nbn}</h1>
<h2>${transferItem.bagId}, version: ${transferItem.objectVersion}</h2>

<p>Archived on ${transferItem.tar.archivalDate}</p>

<table>
<tr><td>bagId</td><td>${transferItem.bagId}</td></tr>
<tr><td>objectVersion</td><td>${transferItem.objectVersion}</td></tr>
<tr><td>datastation</td><td>${transferItem.datastation}</td></tr>
<tr><td>dataversePid</td><td>${transferItem.dataversePid}</td></tr>
<tr><td>dataversePidVersion</td><td>${transferItem.dataversePidVersion}</td></tr>
<tr><td>nbn</td><td>${transferItem.nbn}</td></tr>
<tr><td>otherId</td><td>${transferItem.otherId}</td></tr>
<tr><td>otherIdVersion</td><td>${transferItem.otherIdVersion}</td></tr>
<tr><td>swordClient</td><td>${transferItem.swordClient}</td></tr>
<tr><td>swordToken</td><td>${transferItem.swordToken}</td></tr>
<tr><td>ocflObjectPath</td><td>${transferItem.ocflObjectPath}</td></tr>
<tr><td>filepidToLocalPath</td><td>${transferItem.filepidToLocalPath}</td></tr>
</table>
