$('.tag-input').focusout(function(event){
//	if(event.keyCode === 32){
		var tag = event.target;
		$.ajax({
			url : "/jReditt/tags/tag/create",
			type : 'post',
			dataType : "text",
			data : tag.value,
			success : function(data) {
				var id = JSON.parse(data).tagId;
				var name = JSON.parse(data).name;
				document.getElementById(tag.id+".tagId").value = id;
				document.getElementById(tag.id+".name").value = name;
			},
			error: function (request, status, error) {
		        console.log(request.responseText);
		    }
		});
//	} else if(event.keyCode === 8){
		//1. java java8 spring
		//2. diese drei Worte kommen in die Tabelle drunter
		//3. man entfernt java8
		//5. Man holt sichübrigt gebliebene Wörter: currentValues = $('tag').val().sprlit(' ');
		//5. Bei jedem Entfernen, durchsucht man die Tabelle, ob man das zu entfernende Wort auch aus der Tabelle löschen kann
		//8. In der Tabelle übrig gebliebene Wörter werden übertragen
		
//	}

});
