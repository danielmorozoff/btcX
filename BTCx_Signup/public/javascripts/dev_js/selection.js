var Selection;

$(document).ready(function()
{
	Selection = function(){
		this.displayInputs = function(type)
		{
			$('.input-group.'+type).each(function()
			{
				$(this).fadeIn();
			});
		}
		this.hideInputs = function(type){
			$('.input-group.'+type).each(function()
			{
				$(this).fadeOut();
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
			if($(this).text()=='Trader') Selection.hideInputs("merchant");
			else Selection.displayInputs("merchant");

			$(".input-group").next('.message-error').fadeOut();
		});
	});

});