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

function showDot(DOTstring) {
    // provide data in the DOT language
    var parsedData = vis.network.convertDot(DOTstring);

    var data = {
      nodes: parsedData.nodes,
      edges: parsedData.edges
    }

    var options = parsedData.options;

    // you can extend the options like a normal JSON variable:

    // create a network
    var network = new vis.Network(document.getElementById('graph'), data, options);
}

function attemptLoad() {
    $.get( "/internal/popGraphviz", function( data ) {
        if (data && data != 200) showDot(data);
    });
}

setInterval(function(){ attemptLoad(); }, 1000);

</script>

</html>