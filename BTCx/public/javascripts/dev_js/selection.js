var Selection;

$(document).ready(function()
{
	Selection = function(){
		this.displayInputs = function(type)
		{
			$('.'+type).each(function()
			{
				$(this).fadeIn();
			});
		}
		this.hideInputs = function(type,method){
			$('.'+type).each(function()
			{
				$(this).fadeOut(function()
					{
						if(typeof(method) == 'function')
						{
							method();
						}
					});
			});
		}

	}
	Selection = new Selection();
	//Defaults to trader
	$('.type-selection').change(function()
	{	
		$(".input-group input").removeClass('has-error');
		$($(this).selector+' option:selected').each(function()
		{
			if($(this).text()=='Trader')
			{
				Selection.hideInputs("merchant",function()
					{
						$("#panel-signup-info").removeClass('col-md-6');
						$("#panel-signup-info").addClass('col-md-3');
						$("#panel-trader").removeClass('col-md-6');
						$("#panel-trader").addClass('col-md-12');
						$(".trader-fade").fadeIn(0);
					});

			} 
			else
			{
				$(".trader-fade").fadeOut(0);
				Selection.displayInputs("merchant");
				$("#panel-signup-info").removeClass('col-md-3');
				$("#panel-signup-info").addClass('col-md-6');
				$("#panel-trader").removeClass('col-md-12');
				$("#panel-trader").addClass('col-md-6');
			} 

			$(".input-group").next('.message-error').fadeOut();
		});
	});

});