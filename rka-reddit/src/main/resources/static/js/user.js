$(document).ready(function(){
	var user = $('#user').val();
	fetch('/jReditt/profile/information/content/user-pic?user='+user, {method: 'GET'})
	  .then(res=>{return res.blob()})
	  .then(blob=>{
	    var img = URL.createObjectURL(blob);
	    document.getElementById('profilePic').setAttribute('src', img);
	  });
	
	fetch('/jReditt/profile/information/content?user='+user, {method: 'GET'})
	.then(function(response){
		return response.json();
	}).then(function(data){
		if(data.status!==500){
			$('#linkSize').text('Links: ' + data[0]);
			$('#commentSize').text('Comments: ' + data[1]);
			$('#userSince').text('User since: ' + data[2]);
			$('#userName').text('Username: ' + data[3]);
		}else if(data.status===500){
			$('#linkSize').text('Links: ' + data.message);
			$('#commentSize').text('Comments: ' + data.message);
			$('#userSince').text('User since: ' + data.message);
			$('#userName').text('Username: ' + data.message);
		}
		
	});
});

$( "#displayMessage" ).show(function() {
	setTimeout(() => {
		$('#displayMessage').css('display', 'none');
	}, 3000);
});

