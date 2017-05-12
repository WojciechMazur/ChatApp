/**
 * Created by Wojciech on 02.05.2017.
 */

var sessionId= '';
var name='';

var socket_url='192.168.43.163';
var port = '8080';

$(document).ready(function () {
    $("#form_submit, #form_send_message").submit(function (e) {
        e.preventDefault();
        join();
    });
});

 var webSocket;

 function join() {
     if($('#input_name').val().trim().length<=0){
         alert('Enter your name');
     }else {
         name=$('#input_name').val().trim();

         $('#prompt_name_container').fadeOut(1000, function(){
             openSocket();
         });
     }
     return false;
 }


 function openSocket() {
     if(webSocket !== undefined && webSocket.readyState!==WebSocket.CLOSED){
         return;
     }

     webSocket=new WebSocket("ws://"+socket_url+":"+port+"/ChatAppWebService/chat?name="+name);
     webSocket.onopen=function (evemt) {
         $('#message_containter').fadeIn();

         if(evemt.data === undefined)
             return;

     };

     webSocket.onmessage = function (event) {
         parseMessage(event.data);
     };

    webSocket.onclose = function (event) {
        alert('Error! Connection is closed. Try connecting again.');
    };

 }

 function send() {
     var message = $('#input_message').val();

     if(message.trim().length>0){
         sendMessageToServer('message', message);
     }else{
         alert('Please enter message to send');
     }
 }

function closeSocket() {
    webSocket.close();

    $('message_container').fadeOut(600, function () {
        $('#promp_name_container').fadeIn();
        sessionId='';
        name='';

        $('#messages').html('');
        $('p.online_count').hide();
    });
}

function parseMessage(message) {
    var jObj = $.parseJSON(message);

    // if the flag is 'self' message contains the session id
    if (jObj.flag == 'self') {

        sessionId = jObj.sessionId;

    } else if (jObj.flag == 'new') {
        // if the flag is 'new', a client joined the chat room
        var new_name = 'You';

        // number of people online
        var online_count = jObj.onlineCount;

        $('p.online_count').html(
            'Hello, <span class="green">' + name + '</span>. <b>'
            + online_count + '</b> people online right now')
            .fadeIn();

        if (jObj.sessionId != sessionId) {
            new_name = jObj.name;
        }

        var li = '<li class="new"><span class="name">' + new_name + '</span> '
            + jObj.message + '</li>';
        $('#messages').append(li);

        $('#input_message').val('');

    } else if (jObj.flag == 'message') {
        // if the json flag is 'message', it means somebody sent the chat
        // message

        var from_name = 'You';

        if (jObj.sessionId != sessionId) {
            from_name = jObj.name;
        }

        var li = '<li><span class="name">' + from_name + '</span> '
            + jObj.message + '</li>';

        // appending the chat message to list
        appendChatMessage(li);

        $('#input_message').val('');

    } else if (jObj.flag == 'exit') {
        // if the json flag is 'exit', it means somebody left the chat room
        var li = '<li class="exit"><span class="name red">' + jObj.name
            + '</span> ' + jObj.message + '</li>';

        var online_count = jObj.onlineCount;

        $('p.online_count').html(
            'Hello, <span class="green">' + name + '</span>. <b>'
            + online_count + '</b> people online right now');

        appendChatMessage(li);
    }
}


function appendChatMessage(li) {
    $('#messages').append(li);

    $('#messages').scrollTop($('messages').height());
}

function sendMessageToServer(flag, message) {
    var json = '{""}';

    var object = new Object();
    object.sessionId = sessionId;
    object.message=message;
    object.flag=flag;

    json = JSON.stringify(object);
    webSocket.send(json);
}