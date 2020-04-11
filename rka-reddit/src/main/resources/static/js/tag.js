$('.tag-input').focusout(function(event){
		var tag = event.target;
		document.getElementById(tag.id+".tagId").value = null;
		document.getElementById(tag.id+".tagName").value = tag.value;
});
