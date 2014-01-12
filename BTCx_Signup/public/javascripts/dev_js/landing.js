
$(document).ready(function()
{
	$("#loginBtn").click(function()
		{
			Verifier.verifyForm('login');
		});
		
		$(".signupBtn").click(function()
		{
			Verifier.verifyForm('signup');
		});

		$("#contactBtn").click(function()
		{
			Verifier.verifyForm('contact');
		});

		$(".footer ul li a").click(function()
		{
			$(".footer ul li a").removeClass('active');
			$(this).addClass('active');
			var li = $(this).parent();
			var index = li.index();
			if($(this).attr('index') != undefined) index = parseInt($(this).attr('index'));
			$("#carousel-example-generic").carousel(index);
			if(index == 3) 
			{
				if(MapOperator.map == null) 
					{
						MapOperator.map = L.mapbox.map('crypt-map', 'cryptrex.gobkj39b');
						MapOperator.getMarkers();
			    	}	
			}
			$("#carousel-example-generic").carousel('pause');
		});

		$(".carousel-nav").click(function()
		{
			var index = parseInt($(this).attr('index'));
			$("#carousel-example-generic").carousel(index);
			$("#carousel-example-generic").carousel('pause');
		});

		MapOperator = new Map();
        MapOperator.getUserLocation();

   	window.addEventListener("hashchange", function(){
		var obj = $('a[href="'+window.location.hash+'"]'),
			parent = obj.parent().first(),
			index=0;
			
		if(parent.is('li')){
			index = parent.index(); 
		}
		else{
			index = parseInt(obj.attr('index'));
		} 
		console.debug(index)
		$("#carousel-example-generic").carousel(index);
		$('.footer ul li a').removeClass('active');
		obj.addClass('active');
		$("#carousel-example-generic").carousel('pause');
	}, false);
	
	$('#carousel-example-generic').carousel({
  interval:false
});

	});

	    
