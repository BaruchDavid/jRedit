$(function() {
	$("#field_location").autocomplete({
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
			$('#field_location').val(ui.item.value);
			return false;
		}
	});
});

$('#findbutton').click(function(){
	$.ajax({
		url : "/jReditt/links/link/",
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