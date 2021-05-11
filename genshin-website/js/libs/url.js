import * as Conf from '../libs/conf.js';

function absoluteServiceUri(relativeUri) {
	return Conf.servicePath + relativeUri;
}

function relativeServiceUri(absoluteUri) {
	return absoluteUri.substring(Conf.servicePath.length);
}

export {absoluteServiceUri, relativeServiceUri}
