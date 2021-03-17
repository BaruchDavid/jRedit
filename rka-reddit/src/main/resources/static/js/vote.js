$('.upvote, .downvote').on('click', function(){
            const direction = this.className==='upvote' ? 1 : -1;
            const linkId = this.getAttribute('data-id');
            const voteCountVal = $('#votecount-'+this.getAttribute("data-id"));         
            
            fetch('/jReditt/link/'+linkId+'/vote/direction/'+direction+'/votecount/'+voteCountVal.text())
                    .then(response =>{
                        response => response.json();
                    })
                    .then(data => {
                            if(!isNaN(data))
								$(voteCountVal).html(data);
                            }
                        )
                    .catch(
                        err => {
							console.error(err.message);
						}
					);  
			return false;			
        });