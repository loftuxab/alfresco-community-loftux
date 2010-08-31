<div class="address-box">
    <h3>${msg(title)}</h3>
    <div class="clearfix"></div>
    <div style="padding-left: 12px;">
        <p>${asset.description!''}</p>
        <ul>
            <li><strong>${msg('content.details.author')}</strong>: ${asset.properties['cm:author']!''}</li>
            <li><strong>${msg('content.details.published')}</strong>: <#if asset.properties['cmis:lastModificationDate']??>${asset.properties['cmis:lastModificationDate']?string(msg('date.format'))}</#if></li>
            <li><strong>${msg('content.details.size')}</strong>: ${size}</li>
            <li><strong>${msg('content.details.mimeType')}</strong>: ${asset.properties['cmis:contentStreamMimeType']!''}</li>
            <li><strong>${msg('content.details.download')}</strong>: <a href="<@makeurl asset=asset force='short'/>">${asset.properties['cmis:name']!''}</a></li>
        </ul>
    </div>
</div>    
