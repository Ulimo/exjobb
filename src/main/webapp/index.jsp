<%@page import="java.util.List"%>
<%@page import="manager.webdebug.WebServer"%>
<%@page import="manager.webdebug.GlobalData"%>
<%@page import="java.util.Map.Entry"%>
<%

GlobalData data = null;
if(WebServer.contains("globaldata"))
{
	data = (GlobalData)WebServer.getData("globaldata");
}


%>


<html>
<head>
<title>Debug page</title>
<meta>
</meta>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.16.1/vis.min.js"></script>
<link href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.16.1/vis.min.css" rel="stylesheet" type="text/css" />

<style type="text/css">
    #mynetwork {
      float:left;
      width: 600px;
      height: 600px;
      margin:5px;
      border: 1px solid lightgray;
    }
    #config {
      float:left;
      width: 400px;
      height: 600px;
    }
  </style>
</head>

<body>
	<br /> 
	
<div id="mynetwork"></div>
<div id="config"></div>

<p id="selection"></p>

<input type="text" id="news text"/>
<button id="sendButton">Send</button>
<button id="killButton">Kill</button>

<script type="text/javascript">
  // create an array with nodes
  var nodes = new vis.DataSet([
  
  		<%
  	boolean first = true;
	for(Entry<Integer, List<Integer>> entry : data.neighborList.entrySet())
	{
		if(first)
		{
			first = false;
		}
		else
		{
		%>
			,
		<%
		}
	%>
		{id: <%= entry.getKey() %>, label: 'Node <%= entry.getKey() %>'}
	
	<%
	}
	%>
  ]);

  // create an array with edges
  var edges = new vis.DataSet([
  
  	<%
  	first = true;
	for(Entry<Integer, List<Integer>> entry : data.neighborList.entrySet())
	{
		for(Integer i : entry.getValue())
		{
			if(first)
			{
				first = false;
			}
			else
			{
			%>
				,
			<%
			}
			%>
			{from: <%= entry.getKey()%>, to: <%= i %>, arrows:'to'}
			<%
		}
	}
	/*for(Entry<Integer, ArrayList<Integer>> entry : data.fingerList.entrySet())
	{
		for(Integer i : entry.getValue())
		{
			if(first)
			{
				first = false;
			}
			else
			{
			%>
				,
			<%
			}
			%>
			{from: <%= entry.getKey()%>, to: <%= i %>, arrows:'to', color:{color:'red'}}
			<%
		}
	}*/
	%>
  ]);

  // create a network
  var container = document.getElementById('mynetwork');
  var data = {
    nodes: nodes,
    edges: edges
  };
   var options = {
        physics: {
          stabilization: false
        },
        configure: {
          filter:function (option, path) {
            if (path.indexOf('physics') !== -1) {
              return true;
            }
            if (path.indexOf('smooth') !== -1 || option === 'smooth') {
              return true;
            }
            return false;
          },
          container: document.getElementById('config')
        }
      };
  var network = new vis.Network(container, data, options);
  var selectedNode = -1;
   network.on("click", function (params) {
        if(params.nodes[0] != undefined)
        {
          selectedNode = parseInt(params.nodes[0]);
        }
        else
        {
          selectedNode = -1;
        }
        document.getElementById('selection').innerHTML = selectedNode;
   
        //params.event = "[original event]";
        //document.getElementById('eventSpan').innerHTML = '<h2>Click event:</h2>' + JSON.stringify(params, null, 4);
    });
    
    $("#sendButton").click(function(){
        
        if(selectedNode == -1)
          return;
        //Jquery anrop här till den noden till en servlet
    });
    
    $("#killButton").click(function(){
      if(selectedNode == -1)
          return;
        //Jquery för att döda valda noden
    });
    
</script>
</body>
</html>