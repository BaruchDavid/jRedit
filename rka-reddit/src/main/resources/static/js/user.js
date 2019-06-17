$(document).ready(function(){
	fetch(`/jReditt/profile/information/content`, {
		method: 'GET',
		headers: new Headers({
			'Content-Type': 'text/plain',
			'Data-Type' : 'application/json'
		}),
		body: $('#Username').text()
	}).then(
		response => response.json())
	.then(data => {
			console.log('empfangen: ' + data)
			$('#linkSize').text('Links: ' + data[0]);
			$('#commentSize').text('Comments: ' + data[1]);
			$('#userSince').text('User since: ' + data[2]);
		}
	)
	.catch(
		err => console.error(err)
	);    
});