var Tube;

$(document).ready(function()
{
	Tube  = function()
	{
		this.getPanel = function(panel,callback)
		{	
			this.request('get'+panel+'Panel',{},callback);
		}
		this.login = function(data,callback)
		{
			this.request('login',data,callback);
		}
		this.signup = function(data,callback)
		{
			this.request('signup',data,callback);
		}
		this.request = function(url,data,callback)
		{
			try
			{
				$.post(url,data,function(data)
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