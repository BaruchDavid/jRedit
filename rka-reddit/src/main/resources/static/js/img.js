$(document).ready(function(){
    const control = $('#cache').attr('value');
    getPicture(control);
    $('[data-toggle="tooltip"]').tooltip();
});

function getPicture(control){
    const user = $('#userName').attr("title");
    fetch('/jReditt/profile/information/content/user-pic?user='+user,
    { headers: {'Cache-Control': control}})
              .then(res=>{return res.blob()})
              .then(blob=>{
                const img = URL.createObjectURL(blob);
                var image = $('<img>');
                    var div = $('#drop-area');
                    image.one('load', function() {
                      div.css({
                        'width': '12em',
                        'height': '13em',
                        'background-image': 'url(' + this.src + ')'
                      });
                      $('#container').append(div);
                    });
                    image.attr('src', img);
              });
}