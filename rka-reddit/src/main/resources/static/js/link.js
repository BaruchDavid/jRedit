$(function() {
	$("#searchDrop").autocomplete({
		source : function(request, response) {
			$.ajax({
				url : "/jReditt/links/link/search",
				type : 'post',
				dataType : "text",
				data : {
					search : request.term
				},
				success : function(data) {
					response(JSON.parse(data));
				}
			});
		},
		select : function(event, ui) {
			$('#searchDrop').val(ui.item.value);
			return false;
		}
	});
});

function linkSearch('click' function(){
	$.ajax({
		url : "/jReditt/links/link/apiDesignForPathes??",
		type : 'get',
		dataType : "text",
		data : {
			search : request.term
		},
		success : function(data) {
			response(JSON.parse(data));
		}
	});
});

