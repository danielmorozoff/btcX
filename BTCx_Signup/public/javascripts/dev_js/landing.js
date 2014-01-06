var signup;
var login;

$(document).ready(function()
{
	 a=parseFloat(400);// Initial value for the first background (bk 0)
    b=parseFloat(0);// Initial value for the first background (bk 0)
    c=parseFloat(400);// Initial value for the second background (bk 1)
    d=parseFloat(0);// Initial value for the second background (bk 1)
    var scrollTop = $(window).scrollTop();
    var scroll_actually= new Array();// Identifies the X and Y values for the background
 
    $(window).scroll(function(){//This is not the cleanest way to do this, I'm just keeping it short.
	        if(scrollTop>$(this).scrollTop()) // Scroll up
	        {
	            if (getScrollTop()<=1600 && getScrollTop()>=0)// Identifies the position for the first background when a scroll event occurs
	            {
	                    a=a+35;// Position for the first background, it decreases in 35 pixels
	                    b=b+10;// Position for the first background, it decreases in 10 pixels
	                    $('.img_1').css('backgroundPosition', '50% '+a+'px');
	                    $('.bk_0').css('backgroundPosition', '0 '+b+'px');
	            }
	            if (getScrollTop()>=2050 && getScrollTop()<=3650)
	            {
	                    c=c+35;// Position for the second background, it decreases in 35 pixels
	                    d=d+10;// Position for the second background, it decreases in 10 pixels
	                    $('.img_2').css('backgroundPosition', '50% '+c+'px');
	                    $('.bk_1').css('backgroundPosition', '0 '+d+'px');
	            }
	        }
	        else
	        {// Scroll down
	            if (getScrollTop()>=0 && getScrollTop()<=1600)
	            {
	                  a=a-35;// Position for the first background, it decreases in 35 pixels
	                  b=b-10;// Position for the first background, it decreases in 10 pixels
	                  $('.img_1').css('backgroundPosition', '50% '+a+'px');
	                  $('.bk_0').css('backgroundPosition', '0 '+b+'px');
	            }
	                if (getScrollTop()>=2050 && getScrollTop()<=3650)
	            {
	                  c=c-35;// Position for the second background, it decreases in 35 pixels
	                  d=d-10;// Position for the second background, it decreases in 10 pixels
	                  $('.img_2').css('backgroundPosition', '50% '+c+'px');
	                  $('.bk_1').css('backgroundPosition', '0 '+d+'px');
	            }
	        }
	        if (getScrollTop()==0)// Adjusts the positions values and resets them to zero during a scroll up event
	        {
	            a=parseFloat(400);
	            b=parseFloat(0);
	            c=parseFloat(400);
	            d=parseFloat(0);
	            $('.bk_0').css('backgroundPosition', '0 0');
	            $('.bk_1').css('backgroundPosition', '0 0');
	            $('.img_2').css('backgroundPosition', '50% '+400+'px');
	            $('.img_1').css('backgroundPosition', '50% '+400+'px');
	        }
	      scrollTop = $(this).scrollTop();
	    });
		
		
		$("#loginBtn").click(function()
		{
			Verifier.verifyForm('login');
		});
		
		$(".signupBtn").click(function()
		{
			Verifier.verifyForm('signup');
		});

		$(".footer ul li a").click(function()
		{
			$(".footer ul li a").removeClass('active');
			$(this).addClass('active');
			var li = $(this).parent();
			var index = li.index();
			if($(this).attr('index') != undefined) index = parseInt($(this).attr('index'));
			$("#carousel-example-generic").carousel(index);
			if(index == 4) 
			{
				if(streamMap == null) 
					{
						streamMap = L.mapbox.map('crypt-map', 'cryptrex.gobkj39b');
		    			changeLocation(streamMap,[50.945, 9.009],10,function(coordinates)
			    		{
			    			var marker = {'coordinates':coordinates,'type':'shop','title':'CryptREX','description':'A cryptocurrency marketplace - built for a decentralized enconomy','active':true,'location':'Location','symbol':'shop','color':'#000','size':'large'}
			    			addMarker(marker);
			    			addLayer();
			    		});
			    	}	
			}
			$("#carousel-example-generic").carousel('pause');
		});

   	

	});
	function getScrollTop(){ //  Verifies the total sum in pixels of the whole page
	 
	    if(typeof pageYOffset!= 'undefined'){
	        // Most browsers
	        return pageYOffset;
	    }
	    else{
	        var B= document.body; //IE 'quirks'
	        var D= document.documentElement; //IE with doctype
	        D= (D.clientHeight)? D: B;
	        return D.scrollTop;
	    }
	 }
	    