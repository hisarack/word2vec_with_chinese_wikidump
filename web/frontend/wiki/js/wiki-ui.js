var ControlPanel = React.createClass({

   getInitialState: function(){ 
      return {neighborRows:[]};
   },

   handleWikiQuery: function(){
      var queryWord = this.refs.WordText.getDOMNode().value;
      var that = this;
      $.get("http://hisarack.mooo.com/wikiapi?topn=20&word="+queryWord,function(result){

         var matrix = _.chain(result)
                  .map(function(item){
                     return item["vector"];
                  })
                  .value();

         var sims = _.chain(result)
                  .map(function(item){
                     return item["sim"];
                  })
                  .value();

         var words = _.chain(result)
                  .map(function(item){
                     return item["word"];
                  })
                  .value();


         var opt = {};
         opt.epsilon = 10;
         opt.perplexity = 30;
         opt.dim = 2;

         var tsne = new tsnejs.tSNE(opt);
         tsne.initDataDist(matrix);

         for(var k = 0 ; k < 500; k++){
            tsne.step();
         }

         var vectors = tsne.getSolution();
         
         var max_x = _.chain(vectors).map(function(vector){return vector[0]}).max().value();
         var min_x = _.chain(vectors).map(function(vector){return vector[0]}).min().value();
         var max_y = _.chain(vectors).map(function(vector){return vector[1]}).max().value();
         var min_y = _.chain(vectors).map(function(vector){return vector[1]}).min().value();

         var tmpRows = [];
         var neighbors = _.zip(words, vectors, sims)
          .map(function(item){

            var x = item[1][0];
            var y = item[1][1];
            x = (x-min_x)*500/(max_x-min_x)+100;
            y = (y-min_y)*500/(max_y-min_y)+100;
            
            var cx = x.toFixed(2);
            var cy = y.toFixed(2);
   
            console.log(item);

            tmpRows.push(<tr>
                         <td className="col-md-1">{item[0]}</td>
                         <td className="col-md-1">{cx}</td>
                         <td className="col-md-1">{cy}</td>
                         <td className="col-md-1">{item[2]}</td>
                         </tr>);
            
            return {"word":item[0], "x":cx, "y":cy};
          });
          that.setState({neighborRows:tmpRows});
          window.drawGraph(neighbors);
      });
      //window.searchGraph(word);
   },
   render: function(){
      return (
         <div>
         <div className="row">
            請輸入任意中文單詞,我們會幫您找出相似的單詞
            <input type="text" ref="WordText" size="10"/>
            <button type="button" className="btn btn-info" onClick={this.handleWikiQuery}>送出</button>
         </div>
         <div className="row">
            <table className="table table-bordered">
               <thead>
                  <tr>
                     <th className="col-md-3">Word</th>
                     <th className="col-md-3">X</th>
                     <th className="col-md-3">Y</th>
                     <th className="col-md-3">Similarity</th>
                  </tr>
               </thead>
               <tbody>{this.state.neighborRows}</tbody>
            </table>
         </div>
         </div>
      );
   }
});

React.render(
   <ControlPanel />,
   document.getElementById("control-panel")
);
