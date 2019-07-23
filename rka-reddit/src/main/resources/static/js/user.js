$(document).ready(function(){
	fetch('/jReditt/profile/information/content', {
		method: 'GET'
	}).then(function(response){
		return response.json();
	}).then(function(data){
		$('#linkSize').text('Links: ' + data[0]);
		$('#commentSize').text('Comments: ' + data[1]);
		$('#userSince').text('User since: ' + data[2]);
		$('#profilePic').attr('src',data[3]);
		$('#userName').text('Username: ' + data[4]);
	})    
});