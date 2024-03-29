
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

		$("#verificationBtn").click(function()
		{
			Verifier.verifyForm('verification');
		});

		$("#resetPasswordEmailBtn").click(function()
		{
			Verifier.verifyForm('resetPasswordEmail');
		});
		$("#resetPasswordBtn").click(function()
		{
			Verifier.verifyForm('resetPassword');
		});

		$("a[href]").click(function()
		{
			$("a[href]").removeClass('active');
			$(this).addClass('active');
			var li = $(this).parent();
			var index = li.index();
			if($(this).attr('index') != undefined) index = parseInt($(this).attr('index'));
			$("#carousel-example-generic").carousel(index);
			console.debug('href: '+index);
			if(index == 4) 
			{
				if(MapOperator.map == null) 
					{
						MapOperator.map = L.mapbox.map('crypt-map', 'cryptrex.gobkj39b');
						MapOperator.getMarkers();
			    	}	
			}
		});

		$(".carousel-nav").click(function()
		{
			var index = parseInt($(this).attr('index'));
			$("#carousel-example-generic").carousel(index);
		});

		MapOperator = new Map();
        MapOperator.getUserLocation();

   	window.addEventListener("hashchange", function(){
   		console.debug('hash');
		var obj = $('.footer a[href="'+window.location.hash+'"]'),
			parent = obj.parent(),
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
	}, false);
	
	$('#carousel-example-generic').carousel({
  interval:false
});

	});

	    
