
var w = $("#graph-panel").width();
var h = Math.floor(window.screen.height * 0.8);
var vis = d3.select("#graph-panel").append("svg:svg").attr("width", w).attr("height", h);

var drawRelationshipGraph = function() {
   
   var force = d3.layout.force().size([w, h])
   .nodes(nodes)
   force.start();

   vis.append("rect")
      .attr("width", "100%")
      .attr("height", "100%")
      .attr("fill", "gray")
      .attr("fill-opacity", .2)

   var node = vis.selectAll("g.node")
   .data(force.nodes())
   .enter()
   .append("svg:g")
   .attr("class", "node");

   node.append("svg:circle")
   .attr("r",function(d){return 20}) 
   .attr("cx",function(d){return d.x})
   .attr("cy",function(d){return d.y})
   .style("fill", function(d){
      console.log(d.x);
      console.log(d.y);
      console.log(d.label);
      return "#B5C7E9";
   })
   .style("stroke-width", 3);
   //node.call(force.drag);

   var text = vis.selectAll("text.label")
   .data(force.nodes())
   .enter().append("svg:text")
   .attr("x",function(d){return d.x})
   .attr("y",function(d){return d.y})
   .attr("class", "label")
   .attr("fill", "black")
   .style("font-size","12px")
   .text(function(d) { 
      return d.label;  
   });


}

var drawGraph = function(vectors){
   nodes = [];

   d3.selectAll("g.node").remove();
   d3.selectAll("text.label").remove();
   d3.selectAll("rect").remove();

   _.each(vectors, function(vector){
      nodes.push({"label":vector["word"],"x":vector["x"],"y":vector["y"]});
   });
   
   drawRelationshipGraph();
}

