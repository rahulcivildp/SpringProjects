$(document).ready(function(){
		$("#buttonCancel").click(function(){
			window.location = moduleURL;
		});
		
		$("#fileImage").change(function() {
			fileSize = this.files[0].size;
			imageSizeShort = imageSize * 1024;
			
			if (fileSize > imageSizeShort) {
				this.setCustomValidity("You must choose an image less than "+imageSize+" "+sizeScale+"!");
				this.reportValidity();
			} else {
				this.setCustomValidity("");
				showImageThumbnail(this);
			}
		})
	});
	
	function showImageThumbnail(fileInput) {
		var file = fileInput.files[0];
		var reader = new FileReader();
		reader.onload = function(e) {
			$("#thumbnail").attr("src", e.target.result);
		}
		
		reader.readAsDataURL(file);
	}