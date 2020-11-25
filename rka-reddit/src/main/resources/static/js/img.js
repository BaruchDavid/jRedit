$(document).ready(function(){
    getPicture;
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
  formData.append('pic', file)

  fetch(url, {
      method: 'POST',
      body: formData
    }).then(() => {
        console.log('saved!!!');
        getPicture();
    }).catch(function (error) {
       console.log ("error: " + error);
    });

}

function getPicture(){
    const user = $('#userName').attr("title");
    fetch('/jReditt/profile/information/content/user-pic?user='+user, {method: 'GET'})
              .then(res=>{return res.blob()})
              .then(blob=>{
                const img = URL.createObjectURL(blob);
                //document.getElementById('profilePic').setAttribute('src', img);
                var image = $('<img>');
                    var div = $('#drop-area');
                    image.one('load', function() {
                      div.css({
                        'width': this.width,
                        'height': this.height,
                        'background-image': 'url(' + this.src + ')'
                      });
                      $('#container').append(div);
                    });
                    image.attr('src', img);
              });
}