<html>
<head>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css" rel="stylesheet">
</head>
<body>
    <div id="graph"></div>
</body>

<script
  src="https://code.jquery.com/jquery-3.3.1.min.js"
  integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
  crossorigin="anonymous"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.js"></script>

<script>
// provide data in the DOT language
var DOTstring = `digraph {
                 	rankdir=LR;
                 	node [shape = doublecircle]; "AutomatonState(state=28029722)";
                 	node [shape = circle];
                 	"AutomatonState(state=28029722)" -> "AutomatonState(state=28029722)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=28029722)" -> "AutomatonState(state=28029722)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=28029722)" -> "AutomatonState(state=-652741845)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=28029722)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=-652741845)" -> "AutomatonState(state=-652741845)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=-652741845)" -> "AutomatonState(state=28029722)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=-652741845)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=-652741845)" -> "AutomatonState(state=-652741845)" [ label = "AutomatonTransition(label=NONE)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=872243)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=-585764127)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=-146704528)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=-652741845)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=362019036)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=NONE)" ];
                 	"AutomatonState(state=872243)" -> "AutomatonState(state=872243)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=872243)" -> "AutomatonState(state=872243)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=872243)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=872243)" -> "AutomatonState(state=1492700564)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=872243)" -> "AutomatonState(state=872243)" [ label = "AutomatonTransition(label=QUIT)" ];
                 	"AutomatonState(state=-585764127)" -> "AutomatonState(state=-585764127)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=-585764127)" -> "AutomatonState(state=693547898)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=-585764127)" -> "AutomatonState(state=-1024567326)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=-585764127)" -> "AutomatonState(state=2139676937)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=-585764127)" -> "AutomatonState(state=-585764127)" [ label = "AutomatonTransition(label=NONE)" ];
                 	secret_node_0 [style=invis];
                 	"AutomatonState(state=-1024567326)" -> "secret_node_0" [ label = "AutomatonTransition(label=CLICK)" style=dashed, color=grey];
                 	"AutomatonState(state=-1024567326)" -> "AutomatonState(state=1492700564)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=1492700564)" -> "AutomatonState(state=1492700564)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=1492700564)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=1492700564)" -> "AutomatonState(state=-585764127)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=1492700564)" -> "AutomatonState(state=1492700564)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=1492700564)" -> "AutomatonState(state=1419441160)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=1492700564)" -> "AutomatonState(state=1492700564)" [ label = "AutomatonTransition(label=NONE)" ];
                 	secret_node_1 [style=invis];
                 	"AutomatonState(state=-146704528)" -> "secret_node_1" [ label = "AutomatonTransition(label=CLICK)" style=dashed, color=grey];
                 	"AutomatonState(state=-146704528)" -> "AutomatonState(state=362019036)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=-146704528)" -> "AutomatonState(state=1492700564)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	secret_node_2 [style=invis];
                 	"AutomatonState(state=693547898)" -> "secret_node_2" [ label = "AutomatonTransition(label=CLICK)" style=dashed, color=grey];
                 	"AutomatonState(state=693547898)" -> "AutomatonState(state=872243)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=1419441160)" -> "AutomatonState(state=-585764127)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	secret_node_3 [style=invis];
                 	"AutomatonState(state=1419441160)" -> "secret_node_3" [ label = "AutomatonTransition(label=SWIPE)" style=dashed, color=grey];
                 	"AutomatonState(state=2139676937)" -> "AutomatonState(state=2139676937)" [ label = "AutomatonTransition(label=CLICK)" ];
                 	"AutomatonState(state=2139676937)" -> "AutomatonState(state=2139676937)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=2139676937)" -> "AutomatonState(state=872243)" [ label = "AutomatonTransition(label=SWIPE)" ];
                 	"AutomatonState(state=28029722)" [image="/static/result_images/upload-7d967f20-f3bd-4771-ba89-7e9dda5dc596.png" label="" shape="none"];
                 	"AutomatonState(state=-652741845)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-b913663d-75ae-438a-b698-e9135b0614ac.png" label="" shape="none"];
                 	"AutomatonState(state=362019036)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-b7b0a632-d80b-4399-a1b7-62e9a8e1447c.png" label="" shape="none"];
                 	"AutomatonState(state=872243)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-33c921f5-2006-430c-b7c5-9dab0bd7aacf.png" label="" shape="none"];
                 	"AutomatonState(state=-585764127)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-9b44a8c3-4a3b-4db6-abdd-c427b161a066.png" label="" shape="none"];
                 	"AutomatonState(state=-1024567326)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-fcba3dc8-dc68-455c-857e-489aa0e6a267.png" label="" shape="none"];
                 	"AutomatonState(state=1492700564)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-febbd94d-9277-4921-ac3f-740053875d29.png" label="" shape="none"];
                 	"AutomatonState(state=-146704528)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-c3cd2208-13df-4c6f-9309-d341c87a6282.png" label="" shape="none"];
                 	"AutomatonState(state=693547898)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-76654bce-9db0-4b91-a0d7-20a38a8ff4d4.png" label="" shape="none"];
                 	"AutomatonState(state=1419441160)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-b4a9d852-9f6e-468d-9e2f-479a820c7baa.png" label="" shape="none"];
                 	"AutomatonState(state=2139676937)" [image="/home/aaron/Documents/Projects/BilityBuildSystem/AndroidServer/fileDB/upload-37e2802f-e291-459b-b5e7-c0dfdde38021.png" label="" shape="none"];
                 }`;
var parsedData = vis.network.convertDot(DOTstring);

var data = {
  nodes: parsedData.nodes,
  edges: parsedData.edges
}

var options = parsedData.options;

// you can extend the options like a normal JSON variable:
options.nodes = {
  color: 'red'
}

// create a network
var network = new vis.Network(document.getElementById('graph'), data, options);
</script>

</html>