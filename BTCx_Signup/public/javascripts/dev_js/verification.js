var Verifier;
$(document).ready(function()
{
	Verifier = function()
	{
		this.verifyForm = function(formName)
		{
			var form = $('form[name='+formName+']');
			var elements = 0;
			var valid = 0;
			var text = '';
			var postData = {};
			form.find('input[name]').each(function()
			{
				var input = $(this);
				var type = input.attr('type');
				var name = input.attr('name');
				var val = input.val();
				var check = false;
				if(type == 'text')
				{
					if(name == 'email')
					{
						var atpos=val.indexOf("@");
						var dotpos=val.lastIndexOf(".");
						if (atpos<1 || dotpos<atpos+2 || dotpos+2>=val.length) {}
						else {check = true;}
					}
					else if(val != '' && val.length >= 2) check = true;
				}
				else if(type == 'password')
				{
					var pass1 = form.find('input[name=password]');
					var pass2 = pass1;
					if(form.find('input[name=reppassword]').length > 0) pass2 = form.find('input[name=reppassword]');
					if(pass1.val().length >= 2 && (pass1.val() == pass2.val())) check = true;
					else
					{
						pass1.next('.addon-right').find('span').attr('class','glyphicon glyphicon-remove');	
						if(form.find('input[name=reppassword]').length > 0) pass2.next('.addon-right').find('span').attr('class','glyphicon glyphicon-remove');	
					}
				}
				else if(type == 'checkbox')
				{
					val = input.is(':checked');
					if(val) check = true;
				}
				if(check) 
				{
					postData[name] = val;

					input.next('.addon-right').find('span').attr('class','glyphicon glyphicon-ok');
					valid++;
				}
				else
				{
					if(type != 'checkbox') text += 'Please provide a valid '+name+'<br>';
					else text += 'Agree to our Terms of Use and Privacy Policy';

					input.next('.addon-right').find('span').attr('class','glyphicon glyphicon-remove');	
				}
				elements++;
			});
			if(elements == valid)
			{
				$("#"+formName+"-message").html('Processing...');
				
				if(formName.indexOf('signup') >= 0) this.signup(JSON.stringify(postData));	
				else if(formName.indexOf('login') >= 0) this.login(JSON.stringify(postData));	
			}
			else
			{
				$("#"+formName+"-message").html(text);
			}
		}
		this.signup = function(postData)
		{
			Tube.signup(postData,function(data)
					{
						$('#signup-message').html(data);
					});
		}
	}

	Verifier = new Verifier();

});