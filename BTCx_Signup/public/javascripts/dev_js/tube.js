var Tube;

$(document).ready(function()
{
	Tube  = function()
	{
		this.userip = function(callback)
		{
			//http://smart-ip.net/geoip-json?callback=?
			this.request('userIp','',callback);
		}
		this.userlocation = function(callback)
		{
			//http://freegeoip.net/json/ip'
			this.request('userLocation','',callback);
		}
		this.signup = function(data,callback)
		{
			this.request('signupUser',data,callback);
		}
		this.contact = function(data,callback)
		{
			this.request('contact',data,callback);
		}
		this.markers = function(callback)
		{
			this.request('getMarkers','',callback);
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