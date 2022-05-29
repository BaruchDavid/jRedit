$('.tag-input').focusout(function(event){
		var tag = event.target;
		document.getElementById(tag.id+".tagNum").value = null;
		document.getElementById(tag.id+".tagName").value = tag.value;
});
