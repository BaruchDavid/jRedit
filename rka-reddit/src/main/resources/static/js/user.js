$(document).ready(function(){
	const user = $('#userName').attr("name");
	if(user !== undefined){
        fetch ('/jReditt/profile/information/userClickedLinks?user='+user, {method: 'GET'})
            .then(function (response) {
               return response.json();
            }).then (function (data) {
                  let ul = $('#linkHis');
                  if (data.length === 0) {
                    $('#linkHisTitle').css('display', 'none');
                  } else {
                      $.each( data, function( key, value ) {
                          let li = $('<li class="list-group-item">').appendTo(ul);
                          $('<a>',{
                              text: value.title,
                              title: value.title,
                              href: '/jReditt/links/link/' + value.linkId
                          }).appendTo(li);
                      });
                  }
            }).catch (function (error) {
               console.log ("error: " + error);
            });
	}
});

$( "#displayMessage" ).show(function() {
setTimeout(() => {
	$('#displayMessage').css('display', 'none');
}, 3000);
});
	
