import {fetchTag} from './link.js'

$('.tag-input').focusout(function (event) {
    fetchTag(); //abfragen nach dem evtl schon vorhandenen Tag und dann zur tagNum dessen Id hinzufügen, oder null wenn dieser  Tag vorher nicht existiert
    var tag = event.target;
    document.getElementById(tag.id + ".tagNum").value = null;
    document.getElementById(tag.id + ".tagName").value = tag.value;
});
