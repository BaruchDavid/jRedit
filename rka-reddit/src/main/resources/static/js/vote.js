$('.upvote, .downvote').on('click', function(){
            const direction = this.className==='upvote' ? 1 : -1;
            const linkId = this.getAttribute('data-id');
            const voteCount = $('#votecount-'+this.getAttribute("data-id"));
           
            
            
            fetch(`/jReditt/vote/link/${linkId}/direction/${direction}/votecount/${voteCount.text()}`)
                    .then(
                        response => response.json())
                    .then(data => 
                            voteCount.text(data)
                        )
                    .catch(
                        err => console.error(err)
                    );        
        });