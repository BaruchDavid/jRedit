$('#tag').keyup(function(event){
	if(event.keyCode === 32){
		$.ajax({
			url : "/jReditt/tags//tag/create",
			type : 'post',
			dataType : "text",
			data : {
				search : tag.val()
			},
			success : function(data) {
				response(JSON.parse(data));
			}
		});
	}
});