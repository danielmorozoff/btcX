var Tube;

$(document).ready(function()
{
	Tube  = function()
	{
		this.login = function(data,callback)
		{
			this.request('/signin',data,callback);
		}
		this.signup = function(data,callback)
		{
			this.request('/signup',data,callback);
		}
		this.contact = function(data,callback)
		{
			this.request('/contact',data,callback);
		}
		this.markers = function(callback)
		{
			this.request('/getMarkers','',callback);
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
						data = JSON.parse(data);
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