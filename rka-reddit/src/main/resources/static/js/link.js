function fetchTag() {
    $("#tagsInput > input, #field_location").autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "/jReditt/links/link/tags",
                type: 'post',
                dataType: "text",
                data: {
                    search: request.term
                },
                success: function (data) {
                    response(JSON.parse(data));
                }
            });
        },
        select: function (event, ui) {
            let submitLinkTag = event.target.id;
            $('#' + submitLinkTag + ', #field_location').val(ui.item.value);
            return false;
        }
    });
}

$("#displayMessage").show(function () {
    setTimeout(() => {
        $('#displayMessage').css('display', 'none');
    }, 3000);
});

$(document).ready(function () {
    fetchTag();
});


export {fetchTag}
