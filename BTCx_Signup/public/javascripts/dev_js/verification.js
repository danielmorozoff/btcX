var Verifier;
$(document).ready(function()
{
	Verifier = function()
	{
		this.verifyForm = function(formName)
		{
			var form = $('form[name='+formName+']');
			var postData = {'request':formName};
			var elements = 0;
			var valid = 0;

			if(form.find('.type-selection').length > 0)
			{
				var type = form.find('.type-selection').val();
				postData['type'] = type;
			}
			
			form.find('.input-group:not(:hidden) input, .input-group:not(:hidden) textarea').each(function()
			{

				var input = $(this);
				var type = input.attr('type');
				var name = input.attr('name');
				var msg = input.attr('msg');
				var val = input.val();

				var check = false;


				input.closest('.input-group').next('.message-error').remove();

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

				if(input.attr('optional') != undefined) check = true;

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
				$("."+formName+"-message").text('Processing...');
				
				if(formName.indexOf('signup') >= 0) this.signup(JSON.stringify(postData));	
				else if(formName.indexOf('login') >= 0) this.login(JSON.stringify(postData));
				else if(formName.indexOf('contact') >= 0) this.contact(JSON.stringify(postData));	
			}
		}
		this.signup = function(postData)
		{
			console.debug(postData);
			Tube.signup(postData,function(data)
					{
						Verifier.message('signup-message',data.pass,data.message);
					});
		}
		this.contact = function(postData)
		{
			Tube.contact(postData,function(data)
					{
						Verifier.message('contact-message',data.pass,data.message);
					});
		}
		this.message = function(msgclass,pass,message)
		{
			$('.'+msgclass).attr('class',msgclass);
			if(pass) $('.'+msgclass).addClass('success');
			else $('.'+msgclass).addClass('error');
			$('.'+msgclass).text(message);
		}
	}

	Verifier = new Verifier();

});