---


---

<h1 id="draw-images-via-genetic-programming">Draw Images Via Genetic Programming</h1>
<h4 id="team-members">Team Members:</h4>
<ul>
<li>Feng Huang(001230993)</li>
<li>Zixuan Yu   (001263991)</li>
</ul>
<h4 id="team-number--327">Team Number : 327</h4>
<h4 id="the-application-starts-from-neu.edu.info6205.ui.gaapp.java">*The application starts from <a href="https://github.com/BumbleFeng/GeneticDraw/blob/master/src/edu/neu/info6205/ui/GAApp.java">edu.neu.info6205.ui.GAApp.java</a></h4>
<h2 id="problem-inspiration">Problem Inspiration:</h2>
<p>My teammate and I created a genetic algorithm to get an image represented as a collection of <strong>overlapping polygons</strong> of various colors and transparencies. We start from random 100 polygons. In each optimization step, we randomly modify one  parameter (<strong>like color, stacking or position of vertices</strong>) and check whether such new variant looks more like the target image. If it is, we keep it, and continue to mutate this one instead.  Fitness is a sum of pixel-by-pixel <strong>differences from the original image.</strong> <strong>Lower number is better</strong>.</p>
<h3 id="independent-variables">independent variables:</h3>
<ul>
<li>
<ol>
<li>Polygon Number</li>
</ol>
</li>
<li>
<ol start="2">
<li>Point Number</li>
</ol>
</li>
<li>
<ol start="3">
<li>Scale</li>
</ol>
</li>
<li>
<ol start="4">
<li>Cutoff                  (<strong>for parallel processing</strong>)</li>
</ol>
</li>
<li>
<ol start="5">
<li>Max Generation</li>
</ol>
</li>
<li>
<ol start="6">
<li>Generation Gap (<strong>for  parallel processing</strong>)</li>
</ol>
</li>
<li>
<ol start="7">
<li>Survival Rate</li>
</ol>
</li>
<li>
<ol start="8">
<li>Crossover Rate</li>
</ol>
</li>
<li>
<ol start="9">
<li>Mutate Rate</li>
</ol>
</li>
</ul>
<h2 id="implementation-design：">Implementation Design：</h2>
<h3 id="genetic-code"><em>Genetic code:</em></h3>
<p>By changing the parameter (like <strong>color, stacking or position of vertices</strong>) and checking whether such new variant looks more like the target image, we keep the appropriate parameter. By comparing with the pixel of target image using <strong>RGB</strong>, we can get the absolute value of adding difference. Finally, by sorting  <strong>difference(fitness)</strong>  to keep better individuals.</p>
<h3 id="gene-expression"><em>Gene expression:</em></h3>
<p>Each target image made by  <strong>buffered images</strong>, and each <strong>buffered image</strong> is represented as 100 overlapping <strong>polygons</strong> of various colors and transparencies.</p>
<h2 id="function：">Function：</h2>
<h3 id="fitness-function"><em>Fitness function:</em></h3>
<p>Fitness is a sum of <strong>pixel-by-pixel</strong> differences from the original image. Lower number is better. By comparing with the <strong>pixel</strong> of target image using <strong>RGB</strong>, we can get the <strong>absolute value</strong> of adding difference.</p>
<h3 id="crossing-over"><em>Crossing Over:</em></h3>
<p>we use the uniform crossover, which evaluates each bit in the parent strings for exchange with a probability of 0.5. The offspring has approximately half of the genes from first parent and the other half from second parent, although cross over points can be randomly chosen.</p>
<h3 id="selector"><em>Selector:</em></h3>
<p>We use the Tournament selection to choose two parents in crossover. it run several “tournaments” among a few individuals (or “<a href="https://en.wikipedia.org/wiki/Chromosome_(genetic_algorithm)" title="Chromosome (genetic algorithm)">chromosomes</a>”) chosen at random from the population. The winner of each tournament (the one with the best fitness) is selected for <a href="https://en.wikipedia.org/wiki/Crossover_(genetic_algorithm)" title="Crossover (genetic algorithm)">crossover</a></p>
<h3 id="mutation"><em>Mutation:</em></h3>
<p>When we copy the gene from the parents to child, we will get a <strong>random number</strong>. If it lower than <strong>mutation rate</strong>,  one of parameter <strong>(like color, stacking or position of vertices)</strong> will change.</p>
<h3 id="evolution"><em>Evolution</em>:</h3>
<p>The population is stored as a priority queue, and we will sort the population by their fitness. We select a rate of these population. A part of population can be to evaluate, while other population is discarded.  <strong>The first one (the lowest difference) will copy directly</strong>, and paste into next generation. Besides, each population who can be kept has a <strong>random rate</strong> to mutation or crossover, and generate a next generation.</p>
<blockquote>
<p><strong>For example</strong>, the first generation is 100 individuals. We will <strong>sort</strong> them by their fitness, and select <strong>former</strong> 80% (80 individuals) to keep. Each of these individuals will get a number (0-1). When the individual who has number <strong>more than 0.8</strong>, it will crossover, else it will mutate.</p>
</blockquote>
<h3 id="parallel-processing"><em>Parallel Processing:</em></h3>
<p>We use the parallel processing to improve the speed of calculation. We use two variables <strong>(cutoff and gap)</strong>. The population will be divided into <strong>several parts</strong> (each part is a <strong>thread</strong>), each part will get their own generation by evolution function. And after acquiring several generations (gap means several generations), we will take these parts <strong>together</strong> to build a new generation. Later, <strong>repeat</strong> this loop until require the result.</p>

