
<!-- HTML Upload Panel -->
<div class="fileupload-htmldialog-panel">
  <div class="bd">
      <div class="panel-title">Upload with HTML</div>
  </div>
</div>

<!-- FLASH Upload Panel-->
<div class="fileupload-flashdialog-panel">
  <div class="bd">
      <div class="panel-title">Upload with Flash</div>
      <div>

		<form action="/playground/server/upload.php" method="post" id="photoupload" enctype="multipart/form-data">
			<div class="halfsize">
				<fieldset>
					<legend>Select Files</legend>

					<div class="note">
						Photos will be uploaded in a queue, upload them with one click.<br />

						Selected Options: <b>Filetype Only Images, select multiple files, <i>upload queued</i></b>.
					</div>

					<div class="label emph">
						<label for="photoupload-filedata-1">
							Upload Photos:
							<span>After selecting the photos start the upload.</span>
						</label>

						<input type="file" name="Filedata" id="photoupload-filedata-1" />
					</div>

				</fieldset>
			</div>
			<div class="halfsize">
				<fieldset>
					<legend>Upload Queue</legend>

					<div class="note" id="photoupload-status">
						Check the selected files and start uploading.
					</div>

					<ul class="photoupload-queue" id="photoupload-queue">
						<li style="display: none">&nbsp;</li>
					</ul>
				</fieldset>
			</div>

			<div class="clear"></div>

			<div class="footer">
				<input type="submit" class="submit" id="profile-submit" value="Start Upload"/>
			</div>
		</form>
      </div>
  </div>
</div>

