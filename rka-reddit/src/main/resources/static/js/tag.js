$('#tag').keyup(function(event){
	if(event.keyCode === 32){
		var tags = $('#tag').val().trim().split(' ');
		var tag = tags[tags.length-1];
		$.ajax({
			url : "/jReditt/tags/tag/create",
			type : 'post',
			dataType : "text",
			data : tag,
			success : function(data) {
				var id = JSON.parse(data).tagId;
				var name = JSON.parse(data).name;
				for(var i=0; i<4; i++){
					if(document.getElementById("tags"+i+".tagId")!==null){
						if(document.getElementById("tags"+i+".tagId").value === '0'){
							document.getElementById("tags"+i+".tagId").value = id;
							document.getElementById("tags"+i+".name").value = name;
							break;
						}
					}
				}
			},
			error: function (request, status, error) {
		        console.log(request.responseText);
		    }
		});
	}
});