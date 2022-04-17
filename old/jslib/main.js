function include(path){ 
	var a=document.createElement("script");
	a.type = "text/javascript"; 
	a.src=path; 
	var head=document.getElementsByTagName("head")[0];
	head.appendChild(a);
	}
