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
			var postData = {};
			var type = form.find('.type-selection').val();
			console.debug(type);
			form.find('.input-group:not(:hidden) input').each(function()
			{

				var input = $(this);
				var type = input.attr('type');
				var name = input.attr('name');
				var val = input.val();
				var check = false;
				var msg = input.attr('msg');

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
				}
				else if(type == 'checkbox')
				{
					val = input.is(':checked');
					if(val) check = true;
				}

				input.closest('.input-group').next('.message-error').remove();

				if(check) 
				{
					input.removeClass('has-error');
					postData[name] = val;
					valid++;
				}
				else
				{
					input.addClass('has-error');
					input.closest('.input-group').after('<p class="message-error">Please check '+msg+'</p>');
				}

				elements++;
				
			});
			if(elements == valid)
			{
				$("."+formName+"-message").html('Processing...');
				
				if(formName.indexOf('signup') >= 0) this.signup(JSON.stringify(postData));	
				else if(formName.indexOf('login') >= 0) this.login(JSON.stringify(postData));	
			}
		}
		this.signup = function(postData)
		{
			Tube.signup(postData,function(data)
					{
						$('.signup-message').html(data);
					});
		}
	}

	Verifier = new Verifier();

});