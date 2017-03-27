$(function()
{
	console.log('3');// check placeholder browser support
    if (!Modernizr.input.placeholder)
    {
    	console.log('6');
         // set placeholder values
        $(this).find('[placeholder]').each(function()
        {
        	console.log('10');
            if ($(this).val() == '') // if field is empty
            {
            	console.log('13');
                $(this).val( $(this).attr('placeholder') );
            }
        });
        console.log('17');
         // focus and blur of placeholders
        $('[placeholder]').focus(function()
        {
        	console.log('21');
            if ($(this).val() == $(this).attr('placeholder'))
            {
            	console.log('24');
                $(this).val('');
                $(this).removeClass('placeholder');
            }
        }).blur(function()
        {
        	console.log('30');
            if ($(this).val() == '' || $(this).val() == $(this).attr('placeholder'))
            {
            	console.log('33');
                $(this).val($(this).attr('placeholder'));
                $(this).addClass('placeholder');
            }
        });
        console.log('38');
         // remove placeholders on submit
        $('[placeholder]').closest('form').submit(function()
        {
        	console.log('42');
            $(this).find('[placeholder]').each(function()
            {
            	console.log('45');
                if ($(this).val() == $(this).attr('placeholder'))
                {
                	console.log('48');
                    $(this).val('');
                }
            })
        });
     }
});