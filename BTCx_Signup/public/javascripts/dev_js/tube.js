var Tube;

$(document).ready(function()
{
	Tube  = function()
	{
		this.signup = function(data,callback)
		{
			this.request('signupUser',data,callback);
		}
		this.request = function(url,postData,callback)
		{
			console.debug('Sending request: '+url+' '+postData);
			try
			{
				$.post(url,{ usrStr: postData},function(data)
				{
					if(data != null)
					{
						if(typeof(callback) == 'function') callback(data);
					}
				});
			}
			catch(error)
			{
				if(typeof(callback) == 'function') callback(null);
			}
		}
	}

	Tube = new Tube();

});