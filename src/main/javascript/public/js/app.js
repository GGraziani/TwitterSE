$(document).ready(function(){
    window.glob_data = {};

    document.getElementById("submitButton").onclick = function () {
        TwitterSearch();
    };

    document.onkeypress = function(e) {
        e = e || window.event;
        var charCode = (typeof e.which == "number") ? e.which : e.keyCode;

        if (charCode && charCode ==13) {
            TwitterSearch();
        }
    };

    function TwitterSearch() {
        var data = {
            query : $('#s').val()
        };

        doJSONRequest("POST", "/", {}, data, function(out) {

            sortResults(out.data);

            glob_data["data"] = out.data;
            glob_data["num"] = 0;
            document.getElementById("results").innerHTML = glob_data.data.length+' results';

            document.getElementById('resultsDiv').innerHTML = "";

            renderData();

            $(window).scroll(function(){
                if($(window).scrollTop()+$(window).height() > $("#resultsDiv").height()+140)
                    renderData();
            });
        });
    }
});


function toDate(date){
    var str = date.toString();
    var first = str.substring(0,str.indexOf("+"));
    var sec = str.substring(str.indexOf("+")+6, str.length);

    return first+sec;
}

function renderData(){
    var content = "";

    var data = glob_data.data;
    var start = glob_data.num;
    glob_data.num+=10;
    var limit = glob_data.num;



    for(var i=start; i<limit; i++) {
        if (data[i]) {
            var obj = data[i];
            content +=
                '<div class="webResult"><h2>';
            if (obj["user_img"])
                content += '<img  class="userimage" src="' + obj["user_img"] + '"/>';
            else
                content += '<img  class="userimage" src="../img/default_profile.png"/>';

            content +=
                '<a target="_blank">' + obj["user"] + ' </a>' + // username
                '<a target="_blank">@' + obj["screen_name"] + '</a><br><br>' + // screen name
                // '<a target="_blank">' + obj["text"].toString().substring(0, obj["text"].toString().indexOf("https://t.co/")) + '</a>';

                '<a target="_blank">' + obj["text"].toString() + '</a>';

            if (obj["tweetImg"])
                content += '<img  class="tweetImg" src="' + obj["tweetImg"] + '"/>';
            content +=
                '<ul class="icons">' +
                '<i class="fa fa-heart" aria-hidden="true" style="color:red"></i>' + obj["favorite_count"] +
                '<i class="fa fa-retweet" aria-hidden="true" style="color:greenyellow"></i>' + obj["retweet_count"] +
                '<a href="' + obj["url"] + '"><i class="fa fa-external-link" aria-hidden="true" style="color:dodgerblue"></i></a>' +
                '</ul>' +

                '</h2>' +
                '<p id = "date">' + toDate(obj["created_at"]) + '</p>' + // date
                '</div>';
        }
    }

    document.getElementById('resultsDiv').innerHTML += content;


}

function sortResults(data){

    data.sort(function (a,b) {
        return b["score"] - a["score"]
    })
}

// function doSearch(text) {
//     if (window.find && window.getSelection) {
//         document.designMode = "on";
//         var sel = window.getSelection();
//         sel.collapse(document.body, 0);
//
//         while (window.find(text)) {
//             document.execCommand("HiliteColor", false, "yellow");
//             sel.collapseToEnd();
//         }
//         document.designMode = "off";
//     }
// }