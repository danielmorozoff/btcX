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
	$('.input-group:not(.trader)').hide();
	$('.type-selection').change(function()
	{	
		$($(this).selector+' option:selected').each(function()
		{

			if($(this).text()=='Trader'){
				Selection.hideInputs("merchant");
				Selection.displayInputs("trader");
			}
			else{
				Selection.displayInputs("merchant");
				Selection.displayInputs("trader");
			}
		});
	});

});