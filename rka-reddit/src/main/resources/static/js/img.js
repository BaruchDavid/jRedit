$(document).ready(function(){
    const control = $('#cache').attr('value');
    getPicture(control);
});

function getPicture(control){
    const user = typeof $('h2[title=userName]').attr("name") !== 'undefined' ?
    $('h2[title=userName]').attr("name") : $('#userName').attr("name");
    fetch('/jReditt/profile/information/content/user-pic?user='+user,
    { headers: {'Cache-Control': control}})
              .then(res=>{return res.blob()})
              .then(blob=>{
                if(blob.type==='image/jpeg'){
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
                } else {
                    printError("user picture not availible, please contact admin")
                }
              });
}