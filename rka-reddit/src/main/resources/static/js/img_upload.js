$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});


let dropArea = document.getElementById('drop-area');
['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
  dropArea.addEventListener(eventName, preventDefaults, false)
});

function preventDefaults (e) {
  e.preventDefault()
  e.stopPropagation()
};

['dragenter', 'dragover'].forEach(eventName => {
  dropArea.addEventListener(eventName, highlight, false)
});

;['dragleave', 'drop'].forEach(eventName => {
  dropArea.addEventListener(eventName, unhighlight, false)
});

function highlight(e) {
  dropArea.classList.add('highlight')
}

function unhighlight(e) {
  dropArea.classList.remove('highlight')
}

dropArea.addEventListener('drop', handleDrop, false)

function handleDrop(e) {
  let dt = e.dataTransfer
  let files = dt.files

  handleFiles(files)
}

function handleFiles(files) {
  ([...files]).forEach(uploadFile)
}

function uploadFile(file) {
  let url = '/jReditt/profile/information/content/user-pic'
  let formData = new FormData()
  formData.append('formDataWithFile', file);
  formData.append('pictureExtension', file.name.substring(file.name.indexOf('.')+1));
  fetch(url, {
      method: 'POST',
      body: formData
    }).then(response => {
        if (response.status === 201){
            console.log('saved!!!');
            getPicture('no-cache');
        } else {
            response.text().then(function(textBody){
                let msgArray = textBody.split(';');
                let ul = $('<ul>');
                for (i = 0; i < msgArray.length; i++) {
                  var li = $('<li>', {html:msgArray[i]});
                  li.appendTo(ul);
                }
                var div = $('<div>',{class: 'error_msg'});
                ul.appendTo(div)
                $('#user-name').before(div);
                setTimeout(function(){
                    $('.error_msg').css('display','none');
                }, 3000);
            });
        }
    }).catch(function (error) {
       console.log ("error: " + error);
    });

}