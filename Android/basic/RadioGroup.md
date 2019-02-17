### RadioGroup相关知识  

◆ RadioGroup.OnCheckedChangeListener 响应多次  
错误  correctionRadioGroup.check(R.id.rbHasCode);  
正确  ((RadioButton) radioGroup.findViewById(R.id.rbHasCode2)).setChecked(true);  


