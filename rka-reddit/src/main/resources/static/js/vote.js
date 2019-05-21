$('.upvote, .downvote').on('click', function(){
            const direction = this.className==='upvote' ? 1 : -1;
            const linkId = this.getAttribute('data-id');
            const voteCount = $('#votecount-'+this.getAttribute("data-id")).text();
            
            
            fetch(`/jReditt/vote/link/${linkId}/direction/${direction}/votecount/${voteCount}`)
                    .then(response => response.json())
                    .then(data => voteSum.innerHTML = data)
                    .catch(err => console.error(err))            
            // fetch(`/jReditt/links/vote/link/${linkId}/direction/${direction}/votecount/${voteCount}`)
            //         .then(response => response.json())
            //         .then(data => voteSum.innerHTML = data)
            //         .catch(err => console.error(err))

        });