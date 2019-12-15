$(function() {
	var availableTags = [
	      "ActionScript",
	      "AppleScript",
	      "Asp",
	      "BASIC",
	      "C",
	      "C++",
	      "Clojure",
	      "COBOL"
	    ];
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
			console.log(ui.item.value);
			// Set selection
			//$('#searchDrop').val(ui.item.value); // display the selected text
			//$('#searchDrop').val(ui.item.value); // save selected id to input
			return false;
		}
	});
});